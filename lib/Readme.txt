Read me
******************************************************************************************************************************
Project Smile Email API for sending Email with attachment as Receipt 

SEND EMAIL WITH ATTACHMENT - How to Send (Read Steps Below/ Simple 5 steps if are not a developer as well - No need to know any coding skills, even Dumb people can co-relate Last comments are for few people who might have understood who i was referring to :-) :-) 

STEP 1 : Extract the .zip in D:/ Drive
STEP 2 : Connect to Internet (Good if no firewall settings are enabled).
STEP 3 : Download Java and Install(Simple Product Wizard) and Set Env Variables using Below Link
         https://stackoverflow.com/questions/1672281/environment-variables-for-java-installation
STEP 5 : Place all the Receipts inside D:/projectSmileAPI/encryptedPDF Folder and remember  FileName should be of the form ReceiptNo;emailAddress@host.com;MobileNumber OR ReceiptNo;emailAddress@host.com; 
        Example : MOJO1234567890;maazmmd@gmail.com.pdf OR MOJO1234567890;maazmmd@gmail.com;9449115598.pdf
STEP 4 : doble Click on run.bat File
******************************************************************************************************************************


Below Documentation is for Developer/Person Configuring/ structure changes - if any configuration changes
************************ Usage & Requirements ************************************************
************ Requirements ************
#Machine showed to connected to Internet
#Java Should be installed in Machine 
   Windows OS 
    a.Download and Install Java(jdk1.8) from Oracle website
    b.Set up Environment Variables, Kindly refer below Link
    https://stackoverflow.com/questions/1672281/environment-variables-for-java-installation
    
************ Usage ************    
#Extract zip File in D:\ drive. 
After extraction of .zip file.
#Go through MailConfig.xml and enter SMTP Details and other necessary details.
#Run the bat File present in projectSmileAPI Folder. 

## Status.txt File is the log file, prints success and failure

************************ Measures to take care ********************************

#System should be connected to Internet (Good if no firewall settings are enabled).
#FileName convention of Attahment
   MOJO123456;emailid@abcd.com.pdf
#Folder Structure
├── projectSmileAPI
│   ├── encryptedPDF
│   │   └── imageProcessing(Folder) - Images and Email Body Template goes here
│   │   └── All PDF files are placed here 
│   └── ProjectSmile_MailConfig.xml
│   └── projectSmileEmail.jar - User need not know about this file   
│   └── lib(Folder) - User need not know about this file 
└

###If folder is not extracted in D:\ drive then path should be changed in java Program and again new jar File should be exported using IDE (Eclipse or IntelliJ).
#Bat file cannot be run in Mac and Unix OS.
#If Mac and Unix is being used step with ###Tags should be followed.


******************************************************************************************************************************
If any doubts Kindly contact Mohammed Maaz
Reachable at +91-9449115598, e-Mail : maazmmd@gmail.com
