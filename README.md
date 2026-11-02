# KathirGrid — Renewable Energy Resource Allocation System

This package contains the working browser application (Node.js + MongoDB) and a separate Java OOP allocation-engine source file for your project submission.

## 1. Run the website

**Requirements:**
- Node.js 22 LTS or 24 LTS
- MongoDB Community Server running locally
- MongoDB Compass (optional, to view stored records)

1. Extract this ZIP.
2. Open the `KathirGrid_MongoDB` folder in VS Code (the folder containing `package.json`).
3. Start MongoDB Community Server. Compass is only a database viewer; it does not replace the MongoDB server.
4. Double-click `START_KATHIRGRID.bat`, or open a terminal in `KathirGrid_MongoDB` and run:
   ```bash
   npm install
   npm run dev
   ```
5. Open **http://127.0.0.1:5501**.
6. Demo admin login: **admin** / **admin123**.

The batch file installs the Node dependencies on the first run. Keep the terminal open while using the website. The backend API is at `http://127.0.0.1:8081`.

## 2. MongoDB storage

Default connection: `mongodb://127.0.0.1:27017/kathirgrid`.
Open MongoDB Compass and connect to that address. The application stores:
- `consumers` — registered consumers and their daily demand.
- `dailygenerations` — one saved source-generation document per date (Solar 01, Solar 02, Wind).
- `dailyallocations` — date-wise allocation, unmet demand and curtailed energy.
- `intervalrecords` — up to 24 hourly documents per date, including all three source values and consumer-level allocation details.

### Save daily energy sources

1. Sign in to the website.
2. Open **Daily Generation**.
3. Select a date and enter the Solar 01, Solar 02 and Wind generation values in kWh.
4. Click **Save daily generation**. The app saves that date's generation and recalculates/saves the 24 hourly records and daily allocation report.
5. Select the same date later to search its saved records.

For the sample date **5 October 2026**, the first-time sample values are Solar 01 = 24 kWh, Solar 02 = 18 kWh and Wind = 12 kWh. These are sample/simulated values, not live sensor readings. Click **Run & Save 24 Intervals** if you want to explicitly regenerate its hourly records.

The **Generate next day** action advances one day, creates simulated source values, and stores the generation and hourly records. It does not represent real physical sensor readings.

## 3. Java source file

`java/KathirGrid.java` is a standalone Java OOP demonstration of the priority-allocation engine. It includes abstract consumer loads, divisible/indivisible load subclasses, encapsulated demand fields, a `PriorityQueue`, and interval-level unmet/curtailed reporting.

Compile and run it with JDK 17 or later:
```bash
cd java
javac KathirGrid.java
java KathirGrid
```

**Important:** The website itself is launched by the Node.js + MongoDB application above. The Java file is included as the Java implementation/source-code deliverable; it is not the web server and does not write to the website's MongoDB database.

## 4. Troubleshooting

- If the API health URL `http://127.0.0.1:8081/api/health` says `disconnected`, start MongoDB Community Server and confirm it is listening on port 27017.
- If `npm` or `node` is not recognized, install Node.js LTS, close and reopen VS Code/Command Prompt, then check `node -v` and `npm -v`.
- If a port is already in use, close older KathirGrid terminals before restarting.
- Do not close the terminal running `npm run dev` while using the website.
- Change demo credentials before any non-local deployment. This is a local academic demo, not a production-secured system.
