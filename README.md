# Google_Calendar_Search
Hubitat Google Calendar Search

For discussion and more information, visit the <a href="https://community.hubitat.com/t/release-google-calendar-search/71397">[RELEASE] Google Calendar Search</a>.

## Google API Setup
1. Login to the Google Cloud Console: https://console.cloud.google.com
2. In the left navigation, click APIs & Services, and then choose **Create Project** on the right
![image](https://user-images.githubusercontent.com/10900324/115976568-44281e00-a53d-11eb-9d7e-03689c5bb3ac.png)
3. Give your project a name and click **Create**
![image](https://user-images.githubusercontent.com/10900324/115976609-a4b75b00-a53d-11eb-860e-a99b74d2175a.png)
4. In the left navigation, click **Credentials**
5. At the top, click **+ Create Credentials** and choose **OAuth client ID**
![image](https://user-images.githubusercontent.com/10900324/115976721-e1378680-a53e-11eb-8c4b-88cfd55022cb.png)
6. Set Application type to Desktop app, set a Name, and click **Create**
![image](https://user-images.githubusercontent.com/10900324/115976744-0af0ad80-a53f-11eb-99d2-fbeac0d2cd3e.png)
7. In the OAuth client created popup, copy the Client ID and Client Secret into your favorite text editor to use in the GCal Search HE app and click **OK**
![image](https://user-images.githubusercontent.com/10900324/115976760-3d020f80-a53f-11eb-8b5e-85f749ccb395.png)
8. In the top blue search, enter 'calendar' and choose **Google Calendar API** under Marketplace
![image](https://user-images.githubusercontent.com/10900324/115977025-b569d000-a541-11eb-859a-410082044a67.png)
9. Click **Enable**
![image](https://user-images.githubusercontent.com/10900324/115976840-037dd400-a540-11eb-9cd9-83156851f8ed.png)
10. Click the upper left "hamburger" and choose APIs & Services \ **OAuth consent screen**
![image](https://user-images.githubusercontent.com/10900324/115977071-5a84a880-a542-11eb-8bdc-f74180ad9cbc.png)
11. Choose External and click **Create**
![image](https://user-images.githubusercontent.com/10900324/115976626-d7f9ea00-a53d-11eb-8212-66129f4a3dbb.png)
12. Set an App name, User support email, and Developer contact information and click **Save and Continue**
![image](https://user-images.githubusercontent.com/10900324/115976691-6cfce300-a53e-11eb-881b-5e996868c97a.png)
13. Click **Add or Remove Scopes**
14. In the Manually add scopes, enter https://www.googleapis.com/auth/calendar.readonly and click **Add to Table**
![image](https://user-images.githubusercontent.com/10900324/115977112-d121a600-a542-11eb-862b-a206b5d5c3d8.png)
15. Click **Update**
16. Scroll and click **Save and Continue**
17. On Test Users, Scroll and click **Save and Continue**
18. On Summary, Scroll and click **Back to Dashboard**
19. Click Publish App and Confirm
![image](https://user-images.githubusercontent.com/10900324/115977225-f6fb7a80-a543-11eb-88f6-d77d9605c30d.png)

## Hubitat Installation and Setup
1. Back up your hub and save a local copy before proceeding.
2. Install the apps from the "Apps" folder in this repository into the "Apps Code" section of Hubitat:
  * https://raw.githubusercontent.com/HubitatCommunity/Google_Calendar_Search/main/Apps/GCal_Search.groovy
  * https://github.com/HubitatCommunity/Google_Calendar_Search/blob/main/Apps/GCal_Search_Trigger.groovy
3. Install the driver from the "Driver" folder in this repository into the "Drivers Code" section of Hubitat:
  * https://raw.githubusercontent.com/HubitatCommunity/Google_Calendar_Search/main/Driver/GCal_Switch.groovy    
4. Install an instance of app: go to **Apps > Add User App**, choose **GCal Search**
5. Click **Google API Authorization**
6. Enter the Client ID and Secret copied from Step 7 above
7. Click **Step 1: Get Google API User Code**
8. Copy the highlighted code into your computer clickboard
9. Click **Step 2: Authenticate GCal Search**
10. In the popup, enter the copied code and follow the instructions and enter your Google credentials
11. When prompted that Google hasn't verified this app, click **Advanced**
![image](https://user-images.githubusercontent.com/10900324/115977405-e51ad700-a545-11eb-8d6d-3200e16ec29b.png)
12. In the bottle left click Go to Hubitat Calendar (or whatever you named your project)
![image](https://user-images.githubusercontent.com/10900324/115977420-1c898380-a546-11eb-84fd-e90d0d481094.png)
13. Click **Allow**
![image](https://user-images.githubusercontent.com/10900324/115977454-62464c00-a546-11eb-8d65-4be578c5f907.png)
14. You may close the window that says Success! Device Connected
15. Back in HE, click **Step 3: Check Authentication**
16. You will likely see a message "Still waiting for Authentication...", click Check Authentication
17. Click **Done**
18. Navigate back into the GCal Search app and click New Calendar Search at the top and follow the instructions
