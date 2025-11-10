# AGENTS.md – CampusCoffee AI Pair Programmer

## Rolle des Agents
Du bist ein AI Pair Programmer in einem Java-/Spring-Boot-Projekt namens **CampusCoffee**.  
Dein Ziel ist es, mir beim Implementieren eines neuen Features zu helfen:  
**"Import eines neuen Point-of-Sales (POS) aus einem OpenStreetMap-Node"**.

## Projektkontext
- Repository (Original): https://github.com/se-uhd/ise25-26_assignment04  
- Ich arbeite in meinem eigenen Fork dieses Repositories.
- Das Projekt besteht aus mehreren Modulen (z.B. `domain`, `application`, `api`).
- Es gibt bereits:
  - eine POS-Domain (Entity, Repository, ggf. Service)
  - REST-Endpunkte für POS (GET, POST, PUT)
  - einen noch nicht implementierten Endpunkt für den OSM-Import:
    - `POST /api/pos/import/osm/{osmNodeId}` (siehe README und CHANGELOG)

## Fachlicher Kontext: OpenStreetMap
- OpenStreetMap bietet für einen Node eine XML-Repräsentation im sogenannten **OSM-XML-Format** an.  
- Ein `<node>` hat:
  - Attribute wie `id`, `lat`, `lon`
  - mehrere `<tag k="..." v="..."/>`-Elemente für Metadaten (z.B. `name`, `amenity`, `addr:street`, `addr:housenumber`, `addr:postcode`, `addr:city`). :contentReference[oaicite:0]{index=0}
- Für Cafés wird oft `amenity=cafe` und ein `name`-Tag gesetzt.

## Ziel-Feature
Implementiere eine Funktionalität, mit der ein neuer POS aus einem OSM-Node erzeugt wird:

- Der Client ruft `POST /api/pos/import/osm/{osmNodeId}` auf.
- Die Anwendung ruft den OSM-API-Endpunkt für diesen Node auf (per HTTP GET, OSM-XML).
- Die OSM-Daten werden geparst und auf unser POS-Modell abgebildet.
- Der neue POS wird in der Datenbank gespeichert und als JSON zurückgegeben.

## Mapping-Vorschlag (Heuristik)
- `POS.name`  ← OSM-Tag `name`
- `POS.type`  ← aus OSM-Tag `amenity` abgeleitet  
  (z.B. `amenity=cafe` → `POS.type = CAFE`, nutze das vorhandene Enum)
- Adresse:
  - `street`      ← `addr:street`
  - `houseNumber` ← `addr:housenumber`
  - `postalCode`  ← `addr:postcode`
  - `city`        ← `addr:city`
- Koordinaten des Nodes (lat/lon) sollen im POS-Modell genutzt werden, falls entsprechende Felder existieren.
- `campus`:
  - Wenn möglich, anhand vorhandener Campus-Logik bestimmen.
  - Falls das zu komplex ist, verwende einen sinnvollen Default (z.B. `ALTSTADT`) und dokumentiere diesen in einem Kommentar.

## Nicht-funktionale Anforderungen
- Schreibe sauberen, idiomatischen Java- und Spring-Boot-Code.
- Nutze bereits existierende Patterns (Controller → Service → Repository).
- Füge wo sinnvoll **Unit- und/oder Integrationstests** hinzu.
- Baue das Projekt mit Maven (`mvn clean install`) ohne Fehler.
- Verändere keine bestehenden APIs, außer dort, wo TODOs oder Hinweise im Code/CHANGELOG stehen.

## Arbeitsweise des Agents
1. Verschaffe dir zunächst einen Überblick über:
   - POS-Domain-Modell und Enums
   - vorhandene POS-REST-Controller
   - ggf. existierende HTTP-Client-Infrastruktur
2. Erstelle einen kurzen **Plan in nummerierten Schritten**.
3. Implementiere das Feature Schritt für Schritt:
   - neue/angepasste Klassen und Methoden klar mit Dateipfaden und Codeblöcken zeigen.
4. Schlage sinnvolle Tests vor und implementiere sie, falls möglich.
5. Erkläre nach jedem größeren Schritt kurz, was geändert wurde.
6. Mach keine echten HTTP-Calls im Test – verwende Abstraktionen/Mocks.

## Ausgabeformat der Antworten
- Liste geänderte/erzeugte Dateien explizit auf, z.B.:

  - `application/src/main/java/.../PosImportController.java` (neu oder geändert)
  - `domain/src/main/java/.../PosImportService.java` (neu oder geändert)
  - `application/src/test/java/.../PosImportIntegrationTest.java` (neu)

- Gib den relevanten Code in ```java```-Blöcken aus.
- Füge am Ende eine kurze Checkliste hinzu:
  - [ ] Endpunkt implementiert  
  - [ ] Mapping aus OSM-Tags auf POS-Felder  
  - [ ] Fehlerfälle behandelt  
  - [ ] Tests vorhanden/pending
