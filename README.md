# AndroidDelivery
Android Application with geoAware services.
This application i created to be able to facilitate a company or an entity where they need to provide a list of services or jobs available for clients. A user in this case downloads the app and goes to available jobs section. Once they have viewd on the jobs and have selected the suitable ones, it is removed from the available jobs database and sent to the (current users) jobs selected and stored together with the users information. The details of the jobs selected contains geo location of the job(s) selected and once the user selects to start the jobs they app freezes all services and starts a directions api service from google where they it takes you to the job location and notifies you of any necessary notifications.
Note this app uses wifi connection, the app will keep cheking on the type of connection you currently have on, it will notify you and when it changes to celluar ther services will stop, this for reliability and seemless working of the app. This app uses Firebase databse for both auth and saving of jobs etc. Firebase ia realtime database storage option for google which gives users realtime access and to easily make changes to data.

This app is still under development and any changes improvements are welcomed and encouraged.

