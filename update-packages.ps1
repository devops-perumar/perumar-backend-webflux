# Carpetas base
$baseMain = "src\main\java\pe\edu\perumar\perumar_backend\academico"
$baseTest = "src\test\java\pe\edu\perumar\perumar_backend\academico"

# Mapeo de carpetas a package
$map = @{
    "materias" = "pe.edu.perumar.perumar_backend.academico.materias"
    "carreras" = "pe.edu.perumar.perumar_backend.academico.carreras"
    "ciclos"   = "pe.edu.perumar.perumar_backend.academico.ciclos"
}

foreach ($k in $map.Keys) {
    $targetPackage = $map[$k]

    # Archivos en main
    Get-ChildItem -Path "$baseMain\$k" -Recurse -Include *.java | ForEach-Object {
        (Get-Content $_.FullName) -replace 'package\s+pe\.edu\.perumar\.perumar_backend\.[^;]+;', "package $targetPackage;" | Set-Content $_.FullName
        Write-Host "Actualizado package en $($_.FullName)"
    }

    # Archivos en test
    Get-ChildItem -Path "$baseTest\$k" -Recurse -Include *.java | ForEach-Object {
        (Get-Content $_.FullName) -replace 'package\s+pe\.edu\.perumar\.perumar_backend\.[^;]+;', "package $targetPackage;" | Set-Content $_.FullName
        Write-Host "Actualizado package en $($_.FullName)"
    }
}
