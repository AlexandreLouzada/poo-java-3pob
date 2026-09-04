# Compila todos os topicos de exercicios e roda os testes JUnit (16 e 99).
# Uso:  powershell -ExecutionPolicy Bypass -File scripts/compilar_todos.ps1
$ErrorActionPreference = 'Continue'
$repo = Split-Path -Parent $PSScriptRoot
$ex   = Join-Path $repo 'exercicios'
$lib  = Join-Path $repo 'lib'

function Invoke-Check {
    param([string]$Dir)
    $files = @(Get-ChildItem -LiteralPath $Dir -Filter *.java -File)
    if ($files.Count -eq 0) { return }
    $out = Join-Path $env:TEMP ("out_" + [IO.Path]::GetFileName($Dir))
    if (Test-Path $out) { Remove-Item -LiteralPath $out -Recurse -Force }
    New-Item -ItemType Directory -Path $out | Out-Null
    $cpArgs = if ($needJars) { ,@('-cp', $jcp) } else { ,@() }
    & javac -encoding UTF-8 -nowarn @cpArgs -d $out $files.FullName 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) { Write-Host ("OK   " + [IO.Path]::GetFileName($Dir)) }
    else { Write-Host ("ERRO " + [IO.Path]::GetFileName($Dir) + " - rode javac na pasta para detalhes") }
}

function Run-Tests {
    param([string]$Folder)
    $out = Join-Path $env:TEMP ("out_" + $Folder)
    $files = @(Get-ChildItem -LiteralPath (Join-Path $ex $Folder) -Filter *.java -File)
    & javac -encoding UTF-8 -nowarn -cp $jcp -d $out $files.FullName 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) { Write-Host ("ERRO compilacao de testes em " + $Folder); return }
    $standalone = Join-Path $lib 'junit-platform-console-standalone-1.10.2.jar'
    $cmd = 'java -jar "' + $standalone + '" execute --class-path "' + ($jcp + ';' + $out) + '" --scan-class-path --details=summary 2>nul'
    $r = cmd /c $cmd
    $line = (($r | Select-String 'tests successful') | ForEach-Object { $_.Line }) -join ''
    Write-Host ("Testes (" + $Folder + "): " + $line.Trim())
}

$needJars = $false
$folders = Get-ChildItem -LiteralPath $ex -Directory | Select-Object -ExpandProperty Name
$jcp = ''
if ($folders -contains '16-testes-unitarios-junit-mockito') { $needJars = $true }

if ($needJars) {
    New-Item -ItemType Directory -Path $lib -Force | Out-Null
    $urls = @{
        'junit-jupiter-api-5.10.2.jar'              = 'https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.2/junit-jupiter-api-5.10.2.jar'
        'junit-jupiter-params-5.10.2.jar'           = 'https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-params/5.10.2/junit-jupiter-params-5.10.2.jar'
        'junit-platform-commons-1.10.2.jar'         = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-commons/1.10.2/junit-platform-commons-1.10.2.jar'
        'opentest4j-1.3.0.jar'                      = 'https://repo1.maven.org/maven2/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar'
        'apiguardian-api-1.1.2.jar'                 = 'https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar'
        'mockito-core-5.16.0.jar'                   = 'https://repo1.maven.org/maven2/org/mockito/mockito-core/5.16.0/mockito-core-5.16.0.jar'
        'mockito-junit-jupiter-5.16.0.jar'          = 'https://repo1.maven.org/maven2/org/mockito/mockito-junit-jupiter/5.16.0/mockito-junit-jupiter-5.16.0.jar'
        'byte-buddy-1.17.5.jar'                     = 'https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.17.5/byte-buddy-1.17.5.jar'
        'byte-buddy-agent-1.17.5.jar'               = 'https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.17.5/byte-buddy-agent-1.17.5.jar'
        'objenesis-3.3.jar'                         = 'https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar'
        'h2-2.2.224.jar'                            = 'https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar'
        'junit-platform-console-standalone-1.10.2.jar' = 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar'
    }
    foreach ($k in $urls.Keys) {
        $dest = Join-Path $lib $k
        if (-not (Test-Path $dest)) {
            Write-Host ("baixando " + $k)
            curl.exe -s -L -f -o $dest $urls[$k]
            if (-not (Test-Path $dest)) { throw "falha ao baixar $k" }
        }
    }
    $jcp = (Get-ChildItem -LiteralPath $lib -Filter *.jar | Sort-Object Name | Select-Object -ExpandProperty FullName) -join ';'
}

foreach ($f in ($folders | Sort-Object)) {
    if ($f -in @('16-testes-unitarios-junit-mockito', '99-projeto-integrador')) { continue }
    Invoke-Check -Dir (Join-Path $ex $f)
}

if ($needJars) {
    Run-Tests -Folder '16-testes-unitarios-junit-mockito'
    Run-Tests -Folder '99-projeto-integrador'
}
Write-Host 'concluido.'