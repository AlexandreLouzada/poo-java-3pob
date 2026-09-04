import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ============================================================================
// 1. DOMÍNIO & POLIMORFISMO (Módulos 1, 2 e 3)
// ============================================================================

enum StatusFatura { PENDENTE, PAGA, RECUSADA }
