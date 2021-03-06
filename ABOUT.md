Programming Language Choice
--------------------------
I picked the Java programming language for Android because I've been writing with it for years so far and it feels very comfortable when I know a lot about the language I am using. Also my choice was very limited to Java and Kotlin, and I lack basic knowledge about the latter so I'm only left with Java as an option really!

Goal of the project
--------------------
What I had in mind to accomplish from this application was being able to use the MQTT protocol successfully, and it actually turned out to be easy to use under Paho which facilitates the process a lot. However there are some catches to take care of as Paho won't do everything on its own; for example in ConnectionManager:63 I had to come up with that guarding mechanism because the callback was sometimes being called twice, and even thrice! But I only cared about what was needed to get the app of its feet and perform basic on-the-go functionality.

Reflection
--------------
At first I knew nothing about MQTT and IoT frameworks/protocols in general. Before applying I was mostly reluctant because the field was absolutely new to me and I had no past experience in it to support my gut. However I was convident in my Java and Android skills, after all Udacity's Android Developer Nanodegree isn't a joke and it actually taught me a lot about algorithms and approaches to solving problems altogether and not only Android or Java. To be honest, I consider Android as a tool in my career toolkit.

It took me about three whole days to completely understand what MQTT was and what is it used for, and I scanned dozens of code snippets to train my eyes on the protocol and how to use it, however I didn't want to touch any code before I'm mentally ready to dabble with the thing. After theoritically readying myself, and knowing what was required in the task and drawing a little humble roadmap in my mind, I started with baby-steps implementation trying function after function until I constructed a somewhat 'does-the-trick' product.

My testing approach is by no means formal, and I never got together with unit testing actually. Instead I like using the debugger and printing out debugging lines on-the-go to hot-fix things that usually come up while quickly (and mostly, carelessly) implementing large unbroken pieces of code that pop up into my head. I've always heard about developers refraining from testing, however when I test modules and methods my way, it seems more entertaining and educative than sputtering walls of code all over the editor. Maybe because I've never been into massive business projects yet, but so far my self-esteem toward testing is satisfying, as long as testing a piece of code actually has a reason, and it not merely being business/managerial overhead on the developer.
