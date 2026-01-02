# Update Fighting Flow Database From Sheets

This project takes existing data from Google Sheets and updates a database through
a rest API backend.

This is designed to be packaged and shipped using Google Cloud Functions.

There will be a Pub/Sub trigger that will call the function when there have been 
updates to the Sheets document.