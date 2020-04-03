@if "%DEBUG%" == "" @echo off

set file=docker-compose.yml
set mode=production
if "%~1" == "dev" (
	set file=docker-compose-dev.yml
	set mode=development
)

echo Starting containers in %mode% mode...

docker-compose up %file% d


