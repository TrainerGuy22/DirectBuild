ci = [
        queueSize: 4
]

logging = [
        level: "INFO"
]

web = [
        host: "0.0.0.0",
        port: 8080
]

irc = [
        enabled: false,
        host: "irc.esper.net",
        port: 6667,
        nickname: "SimpleCI",
        username: "SimpleCI",
        channels: [
                "#DirectMyFile"
        ],
        commandPrefix: "!",
        admins: []
]

git = [
        logLength: 4
]

security = [
        enabled: false
]