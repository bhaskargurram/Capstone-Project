# Capstone-Project
<b>Android Nanodegree Capstone Project</b>

<p><b>Description</b><br/>
Just saw a detail and wanna snap it with a reminder? or you want to set a reminder with an image which is dear to your heart?<br/>
Then Snap Reminder is the perfect app for you.<br/>
Customize your reminders add details, set a timer, add an image, load image from device.
</p>
<b>Intended User</b><br/>
Any general android user, who wants to set reminders.

<b>Features</b>
<br/>● Set Reminders with images
<br/>● Add a list of tasks for a reminder.
<br/>● Displays flash cards of saved reminders
<br/>● Frequency can be adjusted
<br/>● Uses Facebook's Graph API to get friends birthday reminders
<br/>

<b>Key Considerations</b>
<br>
<b>How will your app handle data persistence?</b><br/>
A Content Provider is created for accessing the database of saved cards.

<b>Describe any corner cases in the UX.</b><br/>
On Starting the application, if there are no saved cards, then a text needs to be displayed on the home screen that “Currently no saved Cards”. 
<br/>If the user presses back button while taking an image, it should return to home screen i.e the screen containing the flash cards
<br/>If the user does not wants to set a reminder with an image then a default image needs to be put.

<b>Describe any libraries you’ll be using and share your reasoning for including them.</b><br/>
https://github.com/wdullaer/MaterialDateTimePicker: A date picker Library to set the date and time to remind the user.

Process of development

<b>Task 1: Project Setup</b>
<br/>● Import the date picker library properly in the Android Studio
<br/>● Configure the strings.xml, colors.xml, values.xml, styles.xml

<br/><b>Task 2: Implement UI for Each Activity and Fragment</b>
<br/>● Build all images like application logo and all to be used in the application
<br/>● Design the Main Activity
<br/>● Design the Detail View Activity
<br/>● Design the Add Reminder Activity
<br/>● Design the Navigation Bar
<br/>● Design the About Developer Activity

<br/><b>Task 3: Implement the Content Provider and Database</b><br/>

<b>Task 4: Implement Main Activity containing flash cards</b><br/>

<b>Task 5: Implement the Add reminder Activity</b>
<br/>● Setting Camera to Snap an Image (ask user with a dialog if he wants a picture with a reminder)
<br/>● Option to select image from Gallery or take a picture
<br/>● Adding Alarm on the reminder.
<br/>● Implementing notification when the reminder alarm is triggered.

<b>Task 6: Connect Main Activity to Add reminder Activity by using Floating Action Button</b>
 
<b>Task 7: Check proper data insertion into the database and implement the Main Activity to retrieve cards from database</b>

<b>Task 8: Implement the Reminder Detail Activity. Also add an option of editing the reminder</b>

<b>Task 9: Add Facebook Graph API to get user's friends birthdays and save them in database</b>

<b>Task 10: Implement the About Us Activity, Share App, Rate App function</b>
