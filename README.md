# RPR LOgger

A logger for TOT! Chat using Roleplay Redux' webhook feature.

# Usage
IMPORTANT
```
TO USE THIS, YOU WILL NEED TO HAVE ACCESS TO THE OPERATION SYSTEM OF YOUR SERVER, AND THAT OPERATION SYSTEM NEEDS TO BE WINDOWS IF YOU WISH TO USE THE PRECOMPILED BINARY. 

You may, of course, compile your own binary or use anything that has a Java Runtime Environment and run it as a normal Java application
```

## Starting the logger
1. Download the latest release from the releases tab
2. On your server, create a new folder "rpr-logger"
3. Put the executable in that folder
4. Next to the executable, create a new file called "config.txt"
5. Open the "config.txt" to configure your application. Add the following lines:
  - `SECRET=<SECRET_VALUE>`, where <SECRET_VALUE> is a secret you define (an arbitrary string, generate one [here](https://passwordsgenerator.net/), don't use special characters
  - `ALL_RELEVANT=true`
6. Save the config.txt
7. Start "rpr-logger.exe"

## Configuring Webhook
In the Roleplay Redux webhook settings, set the following URL for the webhook:
`http://localhost:15194/rpr-webhook/{server}/{secret}`
Replace:
- {server} with a name for your server. Don't use special characters or whitespaces, i.E. TWEL
- {secret} with the secret you wrote into config.txt

Done. Your logger should now run and create logfiles in the folder the executable is in.


