 E-Commerce Microservices Demo (Java Spring Boot + SQLite + JWT)
 

![E-Commerce Microservices Demo](https://github.com/user-attachments/assets/15b94c8a-0c79-4a97-a6ff-aa4d005b0fd6)

cart service added 

![cartadded](https://github.com/user-attachments/assets/3ccc62d1-7f28-4e8d-9d31-916a29ab4c43)

order service added

<img width="702" height="567" alt="image" src="https://github.com/user-attachments/assets/9548a916-226b-457a-8b08-8a53d904ba3d" />
<img width="1087" height="385" alt="image" src="https://github.com/user-attachments/assets/af6004e8-94e7-4ff4-aa64-9c48172ab62d" />
<img width="745" height="562" alt="image" src="https://github.com/user-attachments/assets/c19a2ce0-b7ab-4dd7-adf4-acfd5d9a89b4" />
<img width="1107" height="711" alt="image" src="https://github.com/user-attachments/assets/dc369993-f340-4ebf-86de-ec601c0083f6" />



Projektübersicht

Dieses Projekt ist eine realitätsnahe E-Commerce-Demo, die mit einer Microservice-Architektur umgesetzt wurde.
Ziel des Projekts ist es, moderne Backend-Konzepte wie REST-APIs, JWT-basierte Authentifizierung, serviceeigene Datenbanken und eine klare Trennung der Verantwortlichkeiten praktisch zu demonstrieren.

Das Projekt befindet sich aktuell in aktiver Entwicklung und wird schrittweise erweitert.

 Aktuelle Architektur (Stand: jetzt)

Die Anwendung besteht derzeit aus zwei unabhängigen Microservices, die jeweils eine eigene SQLite-Datenbank verwenden.

 User Service

Verantwortlich für Benutzerverwaltung

Funktionen:

Registrierung (Register)

Anmeldung (Login)

Passwort-Hashing mit BCrypt

Erstellung von JWT Access Tokens

Technologie:

Java 21

Spring Boot

Spring Security

JWT

SQLite (users.db)

REST-Endpoints:

POST /api/ecommerce/user/register

POST /api/ecommerce/user/login

Der User Service ist vollständig entkoppelt und speichert keine Klartext-Passwörter.

 Product Service

Verantwortlich für Produktverwaltung

Funktionen:

Produktliste abrufen

Produktdetails anzeigen

Statische Produktbilder aus /resources/static/images

Technologie:

Java 21

Spring Boot

JPA / Hibernate

SQLite (products.db)

Jedes Produkt enthält u. a.:

Name

Kategorie

Preis

Lagerbestand

Bild-URL

Beschreibung

Frontend (Demo)

Ein einfaches statisches HTML/JavaScript-Frontend

Funktionen:

Anzeige aller Produkte

Produktsuche und Filter

Produktdetail-Dialog

Benutzer-Registrierung & Login

Anzeige des eingeloggten Benutzers

Kommunikation:

REST-Calls zu User- und Product-Service

JWT wird im localStorage gespeichert

Das Frontend dient bewusst nur als Demo-UI, der Fokus liegt auf dem Backend.

Datenbank-Konzept

Jeder Microservice besitzt seine eigene Datenbank:

Service	Datenbank
User Service	users.db
Product Service	products.db

Keine gemeinsame Datenbank
 Klare Service-Isolation
 Microservice-Best-Practice

 Sicherheit

Passwörter werden mit BCrypt gehasht

JWT wird beim Login erzeugt

Benutzerrolle (USER, später ADMIN) ist serverseitig gesetzt

Keine sensiblen Daten im Frontend gespeichert

 Geplante nächste Schritte

Die folgenden Erweiterungen sind bereits geplant:

Cart Service

Benutzerbezogener Warenkorb

JWT-geschützte Endpoints

Eigene SQLite-Datenbank

Order Service

Bestellverwaltung

Status-Tracking (CREATED, PAID, SHIPPED)

RabbitMQ Integration

Asynchrone Kommunikation

Events wie:

ORDER_CREATED

STOCK_UPDATED

EMAIL_NOTIFICATION

Search AI Service (lokal, Python)

Intelligente Produktsuche

TF-IDF + Cosine Similarity

Keine Cloud, keine GPU, komplett lokal

 Ziel des Projekts

Dieses Projekt dient als:

Lernprojekt für Microservices

Praxisbeispiel für Event-Driven Architecture

Portfolio-Projekt für Backend / Java / Spring Boot

Basis für spätere Erweiterungen mit RabbitMQ und AI-Modulen
