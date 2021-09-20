# AndroidDelivery
Android Application with geoAware services.
This application i created is to be able to facilitate a company or an entity where they need to provide a list of services or jobs available for clients. A user in this case downloads the app and goes to available jobs section. Once they have viewed on the jobs and have selected the suitable ones, it is removed from the available jobs database and sent to the (current users) jobs selected and stored together with the users information. The details of the jobs selected contains geo location of the job(s) selected and once the user selects to start the jobs they app freezes all services and starts a directions api service from google where they it takes you to the job location and notifies you of any necessary notifications. I use java Queue collection to implement they scheduling of jobs one ata time and first in first out order
Note this app uses wifi connection, the app will keep cheking on the type of connection you currently have on, it will notify you and when it changes to celluar then the service will stop, this for reliability and seemless working of the app. This app uses Firebase databse for both auth and saving of jobs etc. Firebase is a realtime database storage option for google which gives users realtime access and to easily make changes to data.

# Features implemented
some of the core features implemenmted include:
- Firebase Auth to facilitate registration and Authentication of users
- one Auth activity to host two fragments login and register
- Broadcast Reciver component to detect network changes and notify user
- Anychart library to give beautiful looking reports in charts and bargraphs
- GeoFence Broadcast Receiver to periodically get user fine or coarse current location
- JobIntent Service which is backgroud thread component used to get if a user enters location of job and makes necessary updates to the system, works together this googles GoeFence, Also linked with notification to infom user as well
- Location Service such that if a customer is active and the screen times out or they move away from the application the active job starts a new service and binds to the application such that when the user moves to another application they locations update continues and the user will still be shown its direction
- AsyncTask to fetch directions details from google directions api. to continously show the user where they should be going.
- RecyclerView Adapter to fetch jobs details for both active and selected jobs of the user
- 
This app is still under development and any changes and improvements are welcomed and encouraged.
Feel free to contact me to explain where theres need to.

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
