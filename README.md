# Smart Blood Bank Management System

## Project report preparation guide

This README is a detailed working guide for preparing the **PBL Progress Report – Final** for this project using [`Report template.docx`](./Report%20template.docx). It summarizes the project as implemented, explains how to map it to the report sections, and lists evidence and personal details to add.

Use it as source material, not as a report to submit unchanged. Write the final report in your own words, acknowledge sources you actually consult, and follow your faculty's rubric if it differs from this guide.

---

## Quick start

### Requirements

- JDK and Maven installed and available in `PATH`.
- MySQL Server running.
- A desktop session that can display Java Swing windows.

Create the database once in MySQL:

```sql
CREATE DATABASE blood_bank;
```

From the project folder, run:

```powershell
mvn clean compile exec:java
```

The app initializes its tables and opens the sign-in window. Demo credentials are **admin / 1234**; they are hard-coded for this prototype and are not secure authentication. Configure MySQL with environment variables or a local `.env` file; see [`DATABASE.md`](./DATABASE.md). Never commit `.env` or share database passwords.

### Typical user flow

1. Sign in using the demo credentials.
2. Use the left navigation to move between **Overview**, **Donor directory**, **Blood inventory**, and **Issue blood**. Pages stay inside the same application window.
3. Select **Add donor** to open the registration form. The form checks required fields, age (1–120), and basic phone-number formatting. Saving a donor also adds one unit to the selected blood group in this prototype.
4. Search the donor directory by name, age, gender, blood group, phone, or city.
5. Review the inventory, refresh it when needed, and note the highlighted zero/low-stock groups.
6. For an issue, choose a blood group and check its displayed availability, enter the patient and hospital, then confirm the one-unit issue. A successful issue is saved and the inventory is reduced transactionally.
7. Log out to return to sign-in.

Database reads and writes are performed off the Swing event-dispatch thread where practical, so a slow database should not freeze the interface. If initialization fails, the app reports a database connection error instead of showing a connected dashboard.

---

## 1. Report details to transfer to the template

The template already supplies some academic details. Verify each item before submission.

| Template field | Suggested entry / action |
|---|---|
| Programme and Branch | B.Tech CSE (AI) — confirm the exact official spelling |
| Course Name | Object Orient Programming Using Java — as printed in the template; confirm with faculty if the spelling should be corrected |
| Course Code | Fill in from your course record |
| Faculty Name | Fill in the faculty member's name |
| Assignment Type | Capstone Project — shown in the template |
| Student(s) Name | Fill in the actual student name(s) |
| ERP ID / Roll No. | Fill in the actual ID(s) |
| Team / Group No. | Fill in if applicable |
| Problem Statement / Project Title | **Smart Blood Bank Management System** |
| SDG(s) | Suggested: **SDG 3 — Good Health and Well-Being**. Explain the connection to blood availability and organized blood-bank operations; confirm the SDG choice with your faculty. |
| Reporting Month | Month 3 – Final — shown in the template |
| Date of Submission | Fill in the actual submission date |

For a team submission, complete one row per member in Section 2 and describe each person's real contribution. Do not invent names, IDs, roles, results, or signatures.

## 2. Important template requirements

The attached template says:

- Use **Times New Roman, 12 pt**, **1.15 line spacing**, and **1-inch margins**.
- Keep the section numbers and headings exactly as in the template.
- Submit the report as a **PDF**. The combined size of the three progress-report PDFs must not exceed **500 KB**.
- The Final Report is a **consolidated report**: include the content of Progress Report I and Progress Report II, followed by Sections 11–18. It is not just a new standalone report covering Section 11 onward.
- Remove the template's grey guidance text and any section that is not applicable; retain the required section headings.
- Tables, figure captions, source code, and the reference list do not count toward section word limits. The page limit applies to report-body Sections 2–16, including figures and tables; the title page, references, and appendix are excluded.
- All sources used must be acknowledged. Cite only sources that you actually used and cite in the report.

### Final Capstone limits from the template

| Report portion | Sections | Word limit shown for CP | Body page limit |
|---|---:|---:|---:|
| Progress Report I | 2–8 | 1,200 words | 4 pages |
| Progress Report II | 9–10 | 800 words | 3 pages |
| Final Report additions | 11–16 | 2,000 words | 7 pages |
| **Consolidated Final Report total** | **3–16 across Reports I, II and Final** | **4,000 words** | **14 pages** |

The template gives a ±10% tolerance around word limits. For a final Capstone submission, it lists **at least 12 references** and an **Appendix (Source Code) maximum of 10 pages**. Follow the specific table in the template if your activity type is not Capstone Project. The faculty may provide additional rubric requirements.

## 3. Project at a glance

### Problem area

Blood-bank staff need a reliable way to keep donor details and blood-group stock organized and to respond to requests for available blood. This project demonstrates a small desktop system that brings these tasks into one application. It is an academic prototype; it is not a clinically validated or production blood-bank platform.

### Aim

To develop a Java desktop application that lets an operator sign in, register donors, view saved donor records, check stock for eight blood groups, and issue a unit against a patient and hospital request while saving the successful issue to MySQL.

### Objectives you can adapt

1. Build a graphical desktop interface using Java Swing.
2. Create screens for login, navigation, donor entry, inventory viewing, and blood requests.
3. Store donor data, blood stock, and successful issue records in a MySQL database.
4. Update donor records and stock together when a donor is added.
5. Prevent a blood issue from reducing a stock count below zero, and record successful issues.
6. Demonstrate object-oriented programming, event handling, JDBC/database access, SQL, and transaction handling.

Make the objectives measurable in your report where possible. For example, test that a donor is still listed after restarting the app, or that a request for an empty stock group does not create an issue record.

### Intended users and stakeholders

- **Primary user:** a blood-bank or hospital staff member operating the application.
- **Indirect stakeholders:** donors, patients, hospitals, and the organization responsible for managing inventory.
- The current application has a single hard-coded login; it does not implement separate roles or user accounts.

### Scope and boundaries

**Included:** donor registration, donor list display, stock display for A+, A−, B+, B−, AB+, AB−, O+, and O−, and recording a successful issue with patient name, hospital, blood group, and timestamp.

**Not implemented:** donor eligibility screening, blood testing, component-level inventory, expiry/batch tracking, compatibility checking, appointment scheduling, notifications, reporting/analytics, user-role administration, and integration with hospital systems. Avoid implying that the program makes medical decisions.

## 4. How the application works

1. The `main` entry point calls `DataStore.initialize()`. The application creates its database tables if necessary, inserts starter inventory only for blood groups that do not already have a stock row, loads saved donors, and then opens the login screen.
2. The sign-in screen accepts the credentials currently hard-coded in `LoginFrame`: username `admin`, password `1234`. These are demo credentials, not secure authentication.
3. On successful login, the dashboard keeps navigation in one window and provides an overview, searchable donor directory, blood inventory, donor registration, and blood issue workflow.
4. Adding a donor validates required details, a numeric age, and basic phone formatting before saving. The donor insert and stock update use one database transaction; database work runs outside the UI thread.
5. The donor directory shows records loaded at startup and added during the current run; its search filters across displayed donor details.
6. Inventory shows current quantities, highlights zero and low stock, and can be refreshed. The overview shows live quantities for selected groups.
7. The issue workflow checks the selected group's availability, asks for confirmation before issuing one unit, and records successful issues transactionally. It handles stock changing between the check and issue without allowing a negative quantity.
8. Logging out returns to the sign-in screen. Database startup displays a connection status screen rather than blocking without feedback.

**Important interpretation:** this is a simplified demonstration rule where registering a donor increments inventory. In a real blood bank, donation collection, screening, testing, processing, and release would need to be represented separately. State this limitation in the report.

## 5. Technical design and implementation facts

### Technology

- Java; Maven compiler release configured as Java 8.
- Java Swing and AWT for the graphical user interface.
- JDBC with MySQL Connector/J **9.4.0**.
- MySQL for persistent storage.
- Maven for compiling and launching the application.

### Main source files

| File | Responsibility |
|---|---|
| `main.java` | Initializes persistence and starts the login screen; displays a database connection error if initialization fails. |
| `LoginFrame.java` | Login form and hard-coded credential check. |
| `DashboardFrame.java` | Single-window navigation, overview, searchable donor directory, and logout action. |
| `DonorFrame.java` | Embedded donor registration panel with required-field, age, and phone checks. |
| `BloodStockFrame.java` | Embedded, refreshable inventory panel with zero/low-stock indicators. |
| `BloodRequestFrame.java` | Embedded blood issue panel with availability check and confirmation. |
| `UiStyle.java` | Shared colors, typography, form components, status messages, and inventory tiles. |
| `Donor.java` | Simple data object holding donor name, age, gender, blood group, phone, and city. |
| `DataStore.java` | Database connection, schema setup, donor loading/saving, inventory queries/updates, and issue transactions. |
| `pom.xml` | Maven project configuration, Java compiler settings, execution entry point, and MySQL JDBC dependency. |
| `DATABASE.md` | Database setup, configuration, tables, and useful inspection queries. |

### Database tables

| Table | Stored information | How the application uses it |
|---|---|---|
| `donors` | Auto-increment ID, name, age, gender, blood group, phone, city | Donor creation and loading donor rows at startup |
| `blood_stock` | Blood group primary key and current integer units | Initial stock, donor-related increment, inventory display, issue decrement |
| `blood_requests` | Auto-increment ID, patient name, hospital, blood group, issue timestamp | Records successful issues only |

All three tables are created by the program if absent. The database itself, named `blood_bank` by default, must exist before startup. Initial stock is inserted for missing stock rows only; subsequent starts do not reset existing quantities.

| Blood group | Initial units |
|---|---:|
| A+ | 10 |
| A− | 5 |
| B+ | 8 |
| B− | 4 |
| AB+ | 6 |
| AB− | 3 |
| O+ | 12 |
| O− | 5 |

### Database configuration

The application reads environment variables first, then values from a `.env` file in the project folder, then defaults:

| Setting | Environment variable | Default |
|---|---|---|
| MySQL host | `MYSQL_HOST` | `localhost` |
| MySQL port | `MYSQL_PORT` | `3306` |
| Database name | `MYSQL_DATABASE` | `blood_bank` |
| Username | `MYSQL_USER` | `root` |
| Password | `MYSQL_PASSWORD` | Empty |

Never include a real database password in the report, screenshots, or source control. See [`DATABASE.md`](./DATABASE.md) for setup details.

### Running the project

Prerequisites: JDK, Maven, MySQL Server, and a desktop session capable of displaying Swing windows. Create the database once:

```sql
CREATE DATABASE blood_bank;
```

Then configure the database settings and run from the project folder:

```powershell
mvn clean compile exec:java
```

Default demo login (hard-coded in source): `admin` / `1234`. Do not describe this credential as secure authentication. The app displays a startup status while it initializes MySQL; if the database is unavailable, check the server, database name, and connection settings before retrying.

## 6. Suggested module/component mapping

The template says Capstone Modules 1–5 should correspond to the five project components/rubric categories defined in the Capstone file. That separate rubric is not included in the files reviewed for this README. **Use the official rubric names if you have them**; the suggestions below are a practical mapping to the implemented project, not a replacement for your faculty's rubric.

| Suggested report module | Project content to discuss | Possible evidence |
|---|---|---|
| Module 1: Requirements and system design | Problem, scope, users, features, constraints, screen flow, design decisions | Use-case diagram, flowchart, or requirements table made by the team |
| Module 2: Java OOP and user interface | `Donor` data class, Swing panels/components, event listeners, input checks | Donor form and dashboard screenshots; class diagram if prepared |
| Module 3: MySQL database and JDBC | Schema, connection configuration, table initialization, donor loading | ER diagram or schema/table screenshot with test data anonymized |
| Module 4: Donor and inventory management | Donor insert, donor list, stock display, initial stock rules, donor-stock transaction | Add-donor and inventory screenshots; before/after query results |
| Module 5: Blood requests, integration and validation | Stock check/decrement, request record, integrated flow, tests, limitations | Request success/unavailable screenshots; test-case table and database evidence |

If your earlier Progress Reports already use other module titles, keep those names consistent in the consolidated Final Report and map the implementation to them.

## 7. Section-by-section writing guide for the consolidated Final Report

Use the exact headings in the Word template. The notes below explain what to write and which project facts can support it. The template's word limits apply; do not paste this whole README into the report.

### 1. Title Page

Fill in the course and project details listed in Section 1 of this README. Use the template's student declaration and provide genuine signatures and date. Do not alter the declaration to suggest that generated or external material is original student work.

### 2. Student Details / Team Details

Complete the provided table with each student's name, ERP ID/Roll No., and actual role/contribution. For an individual project, use one row. For a team, add rows only as needed and explain each member's work accurately.

### 3. Executive Summary

Write this last. In a short paragraph, state the operational problem, the Java Swing/MySQL approach, the key features delivered, and the outcome demonstrated. Mention that this is a prototype and avoid claims about clinical readiness, real-world deployment, or measured impact unless you have evidence.

### 4. Introduction

Introduce the need to organize donor information and blood availability, explain the relevance of applying Java OOP and database concepts, and summarize the project's aim and scope. Distinguish a classroom prototype from a complete blood-bank information system.

### 5. Problem Definition

Define the specific workflow problem addressed: donor details, stock visibility, and tracking a successful issue are otherwise difficult to coordinate using an unstructured/manual process. Identify staff as primary operators and patients/hospitals as beneficiaries. State objectives, constraints (single desktop application, MySQL dependency, simplified stock model), success criteria, and the SDG link you have selected.

Possible measurable success criteria include: all eight supported blood groups appear in inventory; donor records persist after restart; adding a donor increments the matching stock; a valid in-stock request decrements stock and records the request; and an empty-stock request does not create an issue. Report actual results only after testing.

### 6. Industry / Application Overview

Describe the real-world context of blood-bank record and inventory management. Discuss existing approaches or systems only after researching and citing credible sources. Then explain the limited educational gap this project addresses: demonstrating a small, integrated donor/inventory/request workflow. Do not claim that professional blood-bank software has been evaluated unless you performed that comparison.

### 7. Module 1: [Official Module Name]

If using the suggested mapping, discuss requirement analysis and design: the intended user, functional scope, constraints, and the workflow from login to donor/inventory/request. Include a diagram your team created and explain its key decisions.

### 8. Module 2: [Official Module Name]

If using the suggested mapping, describe the Java/Swing implementation. Explain the purpose of the single-window dashboard and its embedded panels, event-driven actions, donor data object, and input checks. Include screenshots of the actual interface and explain what each demonstrates.

### 9. Module 3: [Official Module Name]

If using the suggested mapping, explain the MySQL tables and JDBC persistence. Describe startup table initialization, how donor records are loaded, and how credentials are configured. Include a schema/ER diagram and anonymized database evidence if allowed.

### 10. Module 4: [Official Module Name]

If using the suggested mapping, describe donor registration and inventory. Explain the initial quantities and the donor/stock transaction. Show a tested before-and-after example, with actual values from your test database.

### 11. Module 5: [Official Module Name]

If using the suggested mapping, explain the request operation: required patient and hospital fields, selected blood group, guarded stock decrement, request-history row, and transaction behavior. Include the successful issue and unavailable-stock test cases, along with actual outcomes.

### 12. Final Integrated Solution / Testing and Validation

Describe how the Swing screens, `DataStore`, JDBC, and MySQL work together. Add your own test results. A useful test table is below; complete the **Actual result** and **Status** columns after running each test.

| ID | Test | Expected result | Actual result / status |
|---|---|---|---|
| T1 | Start with MySQL running and valid connection settings | Tables are available, missing stock rows receive initial values, login appears | Fill in |
| T2 | Start with MySQL stopped or invalid connection settings | Startup reports a database connection error and does not open the login screen | Fill in |
| T3 | Sign in with `admin` / `1234` | Login succeeds and dashboard opens | Fill in |
| T4 | Sign in with incorrect credentials | Invalid-credentials message appears; dashboard does not open | Fill in |
| T5 | Submit donor form with a required field empty | Inline validation appears; no donor is saved | Fill in |
| T6 | Submit donor form with non-numeric/out-of-range age or malformed phone | Inline validation appears; no donor is saved | Fill in |
| T7 | Add a donor with valid entered fields | Donor appears in directory and matching stock increases by one | Fill in |
| T8 | Search the donor directory for a known donor or blood group | Matching donor rows are shown; clearing search restores all rows | Fill in |
| T9 | Restart app after adding a donor | Donor remains available from the database | Fill in |
| T10 | Request a blood group with stock greater than zero and confirm | Stock falls by one and a successful request row is stored | Fill in |
| T11 | Cancel the issue confirmation | Stock remains unchanged and no request row is stored | Fill in |
| T12 | Attempt an issue when the selected group has zero stock | Issue action is unavailable; stock is not negative and no successful request row is stored | Fill in |
| T13 | Open/refresh inventory after a stock change | Displayed quantities match the database | Fill in |

Record the test setup, starting stock, actions, observed result, and evidence. If behavior differs from the expected result, report it honestly and explain any fix or remaining defect. Do not present the expected-result column as proof that the test passed.

### 13. Expected Benefits / Learning Outcomes

Discuss the potential benefit of keeping donor, inventory, and issue information in one simple interface. Relate learning to Java classes and objects, Swing GUI components, event handling, JDBC, SQL schema design, database transactions, Maven, testing, and project documentation. Describe these as demonstrated or learned outcomes only where they reflect your actual work.

### 14. Challenges Faced

Report challenges your team actually encountered and how they were resolved. Relevant areas to consider—only include those that occurred—are MySQL setup and credentials, JDBC errors, keeping stock and request records consistent, Swing event handling, validating forms, coordinating team contributions, and testing multiple inventory states.

### 15. Future Improvements

Possible extensions include secure user accounts and role-based permissions; duplicate-donor checks; eligibility and screening workflows; inventory batches, components, and expiry dates; issue-history browsing and reporting; accessibility and keyboard-navigation improvements; audit history; and integration with authorized hospital systems. These are recommendations, not existing features.

### 16. Conclusion

Summarize what was implemented, the tests actually completed, the main learning outcomes, and the limitations. Keep the conclusion aligned with evidence in Section 12; do not claim that the system is ready for real clinical use.

### 17. References

The Final Capstone column in the template requires a minimum of 12 sources. Use the citation style recommended by your faculty (IEEE, APA, or Harvard), cite every source in the report body, and include an access date for web sources. Only list sources you consulted. Useful source categories to research include:

- Official Java language and Java SE documentation
- Official Java Swing and JDBC documentation/tutorials
- Official MySQL documentation for SQL, transactions, and Connector/J
- Official Apache Maven documentation
- United Nations Sustainable Development Goal 3 documentation
- Credible health-authority or peer-reviewed sources about blood-bank inventory and transfusion services
- Course notes or textbooks actually used for OOP/database concepts

Do not pad the reference list with uncited or unverified sources. Record exact page titles, authors/organizations, publication dates, URLs, and access dates while researching.

### 18. Appendix (Source Code)

Follow the template's maximum of 10 pages for a Capstone Final Report. Include a repository/project link if available and a compact, relevant selection of commented source code if the faculty requires it. If all source code cannot fit, ask whether a repository link or separate electronic submission is acceptable. Never put `.env` credentials, personal donor information, or database passwords in the appendix.

## 8. Suggested screenshots and supporting evidence

Capture genuine screenshots from your own running application and add concise figure captions. Where possible, use a clean test database and fictional test data; hide personal information and credentials.

1. Sign-in screen (avoid exposing credentials in report screenshots).
2. Overview showing stock at a glance and quick actions.
3. Donor registration panel with fictional test data and a validation example.
4. Searchable donor directory after a donor has been saved.
5. Inventory screen showing available, low, and zero stock.
6. Blood issue screen before confirmation and after a successful request.
7. Cancelled issue or zero-stock state demonstrating that no unit is deducted.
8. MySQL table/schema or carefully anonymized query output supporting persistence.
9. A flowchart, use-case diagram, or ER diagram created for this project.

Use evidence to support specific statements. A screenshot of a screen alone does not demonstrate that database persistence or transaction behavior works; pair it with a test description or database result.

## 9. Limitations and responsible-use notes

The following are visible in the current implementation and should be acknowledged where relevant:

- Login credentials are hard-coded (`admin` / `1234`) and are not secure authentication.
- Donor input checks required fields, age as a whole number from 1–120, and basic phone formatting; these checks do not establish donor eligibility or check for duplicate donors.
- Donor records are held in a static in-memory list after being loaded from the database at startup; this is a simple application design, not a multi-user synchronization mechanism.
- Registering a donor immediately increases stock by one. There is no donation collection, laboratory screening, or release workflow.
- Inventory is a single quantity per blood group; the system does not track individual units, components, expiry, or storage conditions, and it does not determine transfusion compatibility.
- Request history records successful issues; failed/unavailable attempts are not saved as request rows.
- The interface uses Swing layouts and is intended for a small demonstration; it has not had a formal accessibility or usability evaluation.
- The application requires a reachable MySQL server and a pre-created database.

Do not use the application to make clinical decisions or manage real patient/donor operations. A real deployment would require domain validation, privacy/security controls, operational safeguards, and appropriate regulatory review.

## 10. Final submission checklist

- [ ] Official Capstone rubric/module names checked and used consistently.
- [ ] Title-page details, names, IDs, course code, faculty, team number, and date verified.
- [ ] SDG selection justified and references cited.
- [ ] Sections 2–16 use the exact template headings and stay within the applicable limits.
- [ ] Earlier Progress Report I and II content is included in the consolidated Final Report.
- [ ] Testing table contains actual observed results, not just expected outcomes.
- [ ] Screenshots/diagrams are genuine, captioned, readable, and free of sensitive data.
- [ ] Limitations and future work accurately distinguish existing features from proposals.
- [ ] At least 12 relevant references are cited in the report body (Final Capstone requirement).
- [ ] Appendix follows the template page limit and contains no credentials or private data.
- [ ] Declaration and signatures are completed truthfully.
- [ ] PDF is readable, within page/word/file-size limits, and opens correctly.

## Related project documentation

- [`DATABASE.md`](./DATABASE.md) — database setup, tables, connection settings, and useful SQL queries.
- [`Report template.docx`](./Report%20template.docx) — official report format and evaluation guidance.
