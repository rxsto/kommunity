package to.rxs.kommunity.commands.arguments

internal val HastebinArgument = RegexArgument(
    "hastebin.com/blah.kotlin",
    "(?:https?://)?(?:(?:www\\.)?)?(hastebin\\.com|hasteb\\.in|paste\\.helpch\\.at|haste\\.dev|haste\\.devcord\\.xyz|haste\\.schlaubi\\.me)/(?:raw/)?(.+?(?=\\.|\$|/|#))".toRegex() // https://regex101.com/r/u0QAR6/7
)

internal val URLArgument = RegexArgument(
    "google.com",
    "(https?://)?(www\\.)[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,24}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)|(https?://)?(www\\.)?(?!ww)[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,24}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)".toRegex()
)
