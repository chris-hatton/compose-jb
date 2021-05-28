### web-getting-started-war

This example shows how a **Compose for Web** UI can form part of client/server Web-App written completely in Kotlin and packaged inside a standard WAR file, ready for production deployment.

To remain simple and familiar; the UI is the same as the `web-getting-started` **Compose** example; presenting a single number with two buttons alongside, allowing the number to be incremented and decremented.
The key difference is that adjustments to the number are kept in sync across all active web-clients, in near-real-time.

The example further demonstrates:
- Idiomatic structure of a Compose interface, in which the declarative **View** layer is uni-directionally bound to a **View Model**.
- Reactive presentation logic based on Kotlin `Flow`s
- Use of Ktor HTTP GET/POST Requests and WebSockets.

#### Running

Build and (locally) serve the exaple WAR file:
```
./gradlew tomcatRunWar
```
Then visit [http://localhost:8080](http://localhost:8080) in a sensible browser.
