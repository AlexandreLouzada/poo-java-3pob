# Compila e valida os 23 tutoriais: exemplos (estudos de caso) e solucoes (secao 6).
# Tambem roda os testes JUnit do tutorial 21 (exemplos e solucoes).
# Uso:  powershell -ExecutionPolicy Bypass -File scripts/compilar_tutoriais.ps1
$ErrorActionPreference = 'Continue'
$repo = Split-Path -Parent $PSScriptRoot
$lib  = Join-Path $repo 'lib'

$standalone = Join-Path $lib 'junit-platform-console-standalone-1.10.2.jar'

# Cache local ja existente (se houver) com todos os jars evita downloads na rede.
$cachedLib = Join-Path $env:TEMP 'opencode\lib'
if ((Test-Path -LiteralPath $cachedLib) -and (-not (Test-Path -LiteralPath $lib))) {
    New-Item -ItemType Directory -Path $lib | Out-Null
    Copy-Item -Path (Join-Path $cachedLib '*') -Destination $lib -Force
}

# Baixa os jars necessarios (JUnit 5 e H2) para a pasta local lib/, se faltarem.
# (Se nao houver acesso a rede e o cache em $env:TEMP\opencode\lib existir, os jars
#  sao copiados dali automaticamente.)
$urls = @{
    'junit-jupiter-api-5.10.2.jar'              = 'https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.2/junit-jupiter-api-5.10.2.jar'
    'junit-jupiter-params-5.10.2.jar'           = 'https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-params/5.10.2/junit-jupiter-params-5.10.2.jar'
    'junit-jupiter-engine-5.10.2.jar'           = 'https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.2/junit-jupiter-engine-5.10.2.jar'
    'junit-platform-commons-1.10.2.jar'         = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-commons/1.10.2/junit-platform-commons-1.10.2.jar'
    'junit-platform-engine-1.10.2.jar'          = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-engine/1.10.2/junit-platform-engine-1.10.2.jar'
    'junit-platform-launcher-1.10.2.jar'        = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-launcher/1.10.2/junit-platform-launcher-1.10.2.jar'
    'opentest4j-1.3.0.jar'                      = 'https://repo1.maven.org/maven2/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar'
    'apiguardian-api-1.1.2.jar'                 = 'https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar'
    'h2-2.2.224.jar'                            = 'https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar'
    'junit-platform-console-standalone-1.10.2.jar' = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar'
}
foreach ($k in $urls.Keys) {
    $dest = Join-Path $lib $k
    if (-not (Test-Path -LiteralPath $dest)) {
        Write-Host ("baixando " + $k)
        curl.exe -s -L -f -o $dest $urls[$k]
        if (-not (Test-Path -LiteralPath $dest)) { throw "falha ao baixar $k" }
    }
}
$jcp = (Get-ChildItem -LiteralPath $lib -Filter *.jar | Sort-Object Name |
        Where-Object { $_.Name -notlike 'junit-platform-console-standalone*' } |
        Select-Object -ExpandProperty FullName) -join ';'

function Invoke-Compile {
    param([string]$Dir, [string]$ExtraCp, [string]$Label)
    $files = @(Get-ChildItem -LiteralPath $Dir -Recurse -Filter *.java)
    if ($files.Count -eq 0) { return }
    $out = Join-Path $Dir 'out'
    if (-not (Test-Path -LiteralPath $out)) { New-Item -ItemType Directory -Path $out | Out-Null }
    $cp = if ($ExtraCp) { $jcp + ';' + $ExtraCp } else { $jcp }
    & javac -encoding UTF-8 -nowarn -cp $cp -d $out $files.FullName 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) { Write-Host ("OK   " + $Label) }
    else { Write-Host ("ERRO " + $Label + ' - rode javac na pasta para detalhes') }
}

Write-Host '=== EXEMPLOS ==='
for ($n = 1; $n -le 23; $n++) {
    $aula = 'aula-{0:d2}' -f $n
    $dir = Join-Path $repo (Join-Path 'exemplos' $aula)
    if (Test-Path -LiteralPath $dir) { Invoke-Compile -Dir (Join-Path $dir 'src') -Label ('exemplos/' + $aula) }
}

Write-Host '=== SOLUCOES ==='
for ($n = 1; $n -le 23; $n++) {
    $aula = 'aula-{0:d2}' -f $n
    $dir = Join-Path $repo (Join-Path 'solucoes' $aula)
    if (-not (Test-Path -LiteralPath $dir)) { continue }
    $extra = ''
    if ($n -eq 8) {
        $exOut = Join-Path $repo 'exemplos\aula-08\out'
        if (Test-Path -LiteralPath $exOut) { $extra = $exOut }
    }
    Invoke-Compile -Dir (Join-Path $dir 'src') -ExtraCp $extra -Label ('solucoes/' + $aula)
}

Write-Host '=== TESTES JUNIT (aula 21) ==='
foreach ($base in @('exemplos', 'solucoes')) {
    $out = Join-Path $repo ($base + '\aula-21\out')
    if (-not (Test-Path -LiteralPath $out)) { continue }
    $cmd = 'java -jar "' + $standalone + '" execute --class-path "' + ($jcp + ';' + $out) + '" --scan-class-path --details=summary 2>nul'
    $r = cmd /c $cmd
    $line = (($r | Select-String 'tests successful') | ForEach-Object { $_.Line }) -join ''
    Write-Host ($base + ': ' + $line.Trim())
}

Write-Host 'concluido.'