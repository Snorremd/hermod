# Hermóðr

Hermóðr the Brave (English: Hermod) is a Norse messenger god who rode to Hel to
negotiate the release of Balder -
[Wikipedia article](https://en.wikipedia.org/wiki/Herm%C3%B3%C3%B0r).

This lein plugin lets you post notifications to Hipchat and/or Slack when
deploying an artifact. If lein deploys an artifact successfully the plugin will
attempt to post a notification to Hipchat and/or Slack depending on your config.

## Usage

Put `[hermod "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj
and `[leiningen.hermod]` into the `:hooks` vector. You also need to add a
configuration map in your lein profile. In your `~/.lein/profiles.clj` you want
to have a `:hermod` section like this:

```clojure
:hermod {
    :botname "Hermóðr"
    :hipchat {
        :token "yourhipchattoken"
        :room 1234567}
    :slack {
        :webhook "https://hooks.slack.com/services/yourslacktoken"
        :channel "#my-channel" ;; Optional}}
```

You can choose to use both services or just one. Only add configuration for the
service(s) you want to use. The slack `:channel` property is optional. If left
unspecified Slack uses the default room of the webhook. If you want project
specific botname or service configuration you can use the `:profiles` map in
your `project.clj` file. Add the relevant configuration under `:dev` like so:

```clojure
:dev {:hermod {:botname "My-Bot-Name"
               :slack {:channel "#my-other-channel"}}}
```

The plugin posts a notification when you run `lein deploy`.


## Issues and contributions

If you experience any issues with this plugin you can submit issues at the
[project's Github Repository](https://github.com/Snorremd/hermod/issues).

Any help towards improving this module is welcome. If you want to add features
or fix bugs you are welcome to submit a
[pull request](https://github.com/Snorremd/hermod/pulls).


## License

Copyright © 2016 Snorre Magnus Davøen

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
