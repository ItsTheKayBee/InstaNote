# InstaNote
# Motivation- 
During exams, having good notes to refer to is a boon which not everyone has. FInding information from the web related to the plenty of topics is tedious and very time-consuming. It requires one to type their query, get results, visit sites and find the relevant data which they have to manually copy-paste to make their notes for the particular subject which requires several apps. InstaNote does the entire process for you in just 1 step.

# Description-
InstaNote is an android application which uses material design for the UI. This mobile app has a search feature which lets the users type in their search query and brings the top 3 relevant results from the top websites and displays them in the form of notes. The results are fetched using google search API and then the websites are scraped for the relevant article data through the ‘boilerpipe’ library and fed into pinnable notes. The notes keep the source formatting so that they do not look messy. Once the results are fetched, they can be edited and saved for later reference. We can get more results in just a click. The notes store the website link, title and the content so as to organize the stuff. Once a user clicks on the save button, the notes are inserted into the local SQLite database. SQLite database instance lets users easily save (insert), edit (update), view (select) and delete the notes as per user need. This helps the user minimize the time to find the notes and focus more on studying. This app also has widgets in it for ease of access.

# How to install
1. Clone repository
2. Open as Android Studio project.
3. Run the app on emulator or device.
