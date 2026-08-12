![CI](https://github.com/asharameta/barbershop-ai-consultant/actions/workflows/ci.yml/badge.svg)

# LinkifyPDF

A full-stack web application that lets you draw selection boxes on PDF pages and embed **native clickable hyperlink annotations** directly into the PDF file using Apache PDFBox.

---

## Features

- Upload a PDF and preview it in the browser via pdf.js
- Draw rectangular selection areas on any page with your mouse
- Assign URLs, email addresses (`mailto:`), or phone numbers (`tel:`) to each selection
- Backend embeds native PDF link annotations — links work in any PDF viewer
- Saves documents and their link metadata to PostgreSQL
- **My Library** page to browse, preview, and download previously processed PDFs
- Delete documents from the library (removes from DB and disk)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3.4, Java 22 |
| PDF Processing | Apache PDFBox 3.0.4 |
| Database | PostgreSQL + Spring JdbcTemplate |
| Frontend | Vanilla HTML, CSS, JavaScript |
| PDF Rendering | pdf.js 2.16.105 |
| Build Tool | Maven |

---

## Architecture

```
Browser (Front/)
    │
    ├── HomePage  ─── POST /documents ──▶  Spring Boot (:8080)
    │   (draw boxes,                           │
    │    assign URLs)                          ├── PDFBox: embed link annotations
    │                                          ├── Save annotated PDF to disk
    │                                          └── Store metadata in PostgreSQL
    │
    └── LibraryPage ─ GET /documents  ──▶  Spring Boot
        (browse, preview,   GET /documents/{id}/file
         download, delete)  DELETE /documents/{id}
```

The frontend is served as static files (no framework). The backend REST API runs on port `8080`. The database stores document metadata and link selection coordinates.

---

## Prerequisites

- Java 22+
- Maven 3.9+
- PostgreSQL 15+
- A static file server for the frontend (e.g. VS Code Live Server, nginx, or Python's `http.server`)

---

## Setup

### 1. Database

Create a PostgreSQL database and run the schema:

```sql
-- Create database
CREATE DATABASE "LinkifyPDF";
```

Then connect to it and run:

```bash
psql -d LinkifyPDF -f Back/src/main/resources/schema.sql
```

### 2. Backend Configuration

Copy the example config and fill in your values:

```bash
cp Back/src/main/resources/application.properties.example Back/src/main/resources/application.properties
```

Edit `application.properties`:

| Property | Description |
|---|---|
| `spring.datasource.url` | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/LinkifyPDF` |
| `spring.datasource.username` | PostgreSQL username |
| `spring.datasource.password` | PostgreSQL password |
| `spring.servlet.multipart.location` | Absolute path to the directory where PDFs will be stored, e.g. `C:/pdf-storage` |

Make sure the storage directory exists and is writable.

### 3. Run the Backend

```bash
cd Back
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 4. Serve the Frontend

Serve the `Front/` directory from any HTTP server. Using Python:

```bash
cd Front
python -m http.server 5500
```

Or use VS Code **Live Server** extension pointed at `Front/`.

Then open `http://localhost:5500/Pages/HomePage/index.html`.

---

## Usage

1. **Open the Home page** and upload a PDF (click the drop zone or drag & drop)
2. **Draw selection boxes** on the PDF by clicking and dragging
3. **Enter a URL** in the prompt that appears (e.g. `https://example.com`)
4. Repeat for as many links as you want, then click **SEND**
5. **Open My Library** to see your documents
6. Click a document card to preview it — your link areas are highlighted in blue
7. Click **Download** to get the annotated PDF with working hyperlinks

---

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/documents` | Upload a PDF with link selections |
| `GET` | `/documents` | List all documents with their selections |
| `GET` | `/documents/{id}` | Get a single document's metadata and selections |
| `GET` | `/documents/{id}/file` | Download the annotated PDF binary |
| `DELETE` | `/documents/{id}` | Delete a document and its file |

### POST `/documents` — Request

`multipart/form-data` with parts:

| Part | Type | Description |
|---|---|---|
| `file` | `application/pdf` | The PDF file to process |
| `json` | `application/json` | Array of selection objects (see below) |
| `canvasHeight` | `text/plain` | Canvas pixel height used for coordinate mapping |

Selection object schema:
```json
{
  "linkValue": "https://example.com",
  "linkType": "URL",
  "pageNumber": 0,
  "xNorm": 120.5,
  "yNorm": 300.0,
  "wNorm": 200.0,
  "hNorm": 40.0
}
```

> `xNorm`, `yNorm`, `wNorm`, `hNorm` are canvas pixel coordinates at the rendered scale. `pageNumber` is 0-indexed.

---

## Known Limitations / Future Work

- **OptimizerPage** is a placeholder — PDF compression is planned for a future release
- **No authentication** — all documents are publicly accessible; anyone with the API URL can read or delete any document
- **Local disk storage** — files are stored on the server's local filesystem; not suitable for multi-instance deployment
- **Single page preview** — the library viewer renders only page 1; full multi-page navigation is not yet implemented
- **No duplicate detection** — uploading the same file twice creates two separate documents

---

## License

MIT
