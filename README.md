# Project-Smile-API (Email and SMS)

* Implementation done using Java Mail API and Java SMS using Http  
* Email and SMS can be configured to any SMTP/Gateway and send to users

Email Template can be created online using tools [Reference Link](https://beefree.io/)

* All the Configuration are mentioned in Config.xml,

*************************************************************************************************************  
Project Smile Email API for sending Email with attachment as Receipt  

## Prerequisite/SetUp 
##### Machine showed to connected to Internet  
##### Java Should be installed in Machine  
Windows OS  
* Download and Install Java(jdk1.8) from Oracle website  
* Set up Environment Variables, [Reference](https://stackoverflow.com/questions/1672281/environment-variables-for-java-installation)
*********************************************************************************

##### SEND EMAIL WITH ATTACHMENT  
How to Send (Read Steps Below/ Simple 5 steps to be followed for setUp, if you are not a developer as well

##### Download Link [Google Drive](https://drive.google.com/open?id=0By1jo4P_Ud1XLTZuZEM3ZzQxS2s)

* After Downloading the attachment from Google Drive, Extract the .zip in D:/ Drive  
* Connect to Internet (Good if no firewall settings are enabled).  
* Download Java and Install(Simple Product Wizard) and Set Env Variables [Reference]( https://stackoverflow.com/questions/1672281/environment-variables-for-java-installation)  
* Place all the Receipts inside D:/projectSmileAPI/encryptedPDF Folder and remember FileName should be of the form ReceiptNo;emailAddress@host.com;MobileNumber OR ReceiptNo;emailAddress@host.com;  
        Example : MOJO1234567890;maazmmd@gmail.com.pdf OR MOJO1234567890;maazmmd@gmail.com;9449115598.pdf  
* doble Click on run.bat File  
******************************************************************************************************************************   
### Measures to take care 

* Extract zip File in D:\ drive, After extraction of .zip file follow below guidelines
* Go through MailConfig.xml and enter SMTP Details and other necessary details.  
* Place all the pdf documents in D:/projectSmileAPI/encryptedPDF/ folder  
* Run the bat File present in D:/projectSmileAPI Folder.  

##### Status.txt File is the log file, prints success and failure  
##### System should be connected to Internet (Good if no firewall settings are enabled).  
##### FileName convention of Attahment  
   MOJO123456;emailid@abcd.com.pdf - For Only Sending Email  
   OR  
   MOJO123456;emailid@abcd.com;9449115598.pdf - For Email as well as SMS  

##### Folder Structure  
├── projectSmileAPI  
│   ├── encryptedPDF  
│   │   └── imageProcessing(Folder) - Images and Email Body Template goes here  
│   │   └── All PDF files are placed here  
│   └── ProjectSmile_MailConfig.xml  
│   └── projectSmileEmail.jar - User need not know about this file  
│   └── lib(Folder) - User need not know about these file  
└

##### If folder is not extracted in D:\ drive then path should be changed in java Program and again new jar File should be exported using IDE (Eclipse or IntelliJ). 
##### Create runnable jar in Eclipse IDE [Link](https://www.tutorialspoint.com/eclipse/eclipse_create_jar_files.htm)
##### Bat file cannot be run in Mac and Unix OS.  
##### If Mac and Unix is being used step with ###Tags in config as well as Java Program should be changed.  
