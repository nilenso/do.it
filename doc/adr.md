# Architectural Decision Records

**Background:** Do.it is a collaborative todo list app. The aim of the
project is not just to ship the app but to help me learn why and how of
web-development. For every decision I make on the project I should be
able to justify it. Rather than being exhaustive, I am trying to
*reasonable* with these decisions.

---

### **Language**: Clojure

-   The language clojure is build around "simplicity", simple doesn't
     mean *easy* but it means *untangled*. Clojure programs are easy to
     reason about, the language helps us write correct and reliable web
     programs, that scales well. Based on JVM, Clojure has rich and
     mature java infrastructure to build upon, being nilenso's
     primarily language of choice, clojure has worked well for us.

### **Frontend Framework**

**Decision:** Re-frame

-   In re-frame, the frontend is entirely represented by a "world
     state". The views are purely defined by certain properties of the
     "world state" called subscriptions. User can fire "events" which
     can change "world state". When subscriptions changes as a result
     of new "world state", corresponding views changes automatically.
     At each point of time you can inspect the world state, which
     events got fired with what data and what are the resulting changes
     in the subscriptions. This makes it easy to write the frontend and
     reason about it.

-   Familiarly: I have worked with re-frame on time-tracker, I know how
     to work with re-frame and I like working with re-frame.

-   Cognitive load: With re-frame, I can write the frontend in
     clojurescript, which reduces the cognitive load of switching
     languages for backend and frontend.

-   Powerful Debugging tools such as 10x

### **Single Repo v. Multiple Repo**

**Decision:** Single Repo

-   We can share code between backend and frontend.

-   It is easier to do project management

-   Working with git and CI is easier

### **Database:** Postgres

-   It has been our database of choice for the projects I have come
    across.

-   Apparently it is better than other relational databases, see
     [https://di.nmfay.com/postgres-vs-mysql](https://di.nmfay.com/postgres-vs-mysql)
     (I'm yet to read the article)

### **Migration Library:** Ragtime

-   Major contenders for the migration library were
     [ragtime](https://github.com/weavejester/ragtime)
     and [migratus](https://github.com/yogthos/migratus),
     they are quite similar except that in ragtime you specify
     migration as
     \`\<migration\_number\-\<migration\_name\>.{up,down}.sql\`
     whereas in migratus you use use a timestamp instead of migration
     number. Timestamp helps in avoiding merge conflicts if you are
     working on multiple branches simultaneously, though as a sole
     developer on do.it this shouldn't be particularly useful. I find
     timestamps in file names to be rather ugly, hence choosing
     ragtime.

-   Reference:
     [https://adambard.com/blog/clojure-migration-libraries/](https://adambard.com/blog/clojure-migration-libraries/)

### Plaintext Markup Format

**Decision:** Markdown

- Why `org-mode`? Because I am on emacs, and it is the markup language to go for emacs users. It is standarized and is more powerful than markdown. Reference: http://karl-voit.at/2017/09/23/orgmode-as-markup-only/

- Why `markdown`? Because I have been using it for quite long and it is more popular. Otherwise the syntax for both is quite similar and github renders both of them.

### Configuration Management

**Decision:** juxt/aero

**Contenders:** [juxt/aero](https://github.com/juxt/aero), [yogthos/config](https://github.com/yogthos/config), [environ](https://github.com/weavejester/environ)

- Out of all three, only aero provides nested configs, parsing of config variables. With aero we don't need to seperate config files for different profiles and can have parts of config conditional on lein profile. Aero also allows us to use files for passwords instead of using enviornment variables.

### HTTP Client

**Decision:** http-kit

**Contenders:** http-kit, clj-http

http-kit and clj-http looks equivalent, found no strong reason to prefer one over another. http-kit also comes with a http server, so going with it.

### HTTP Server

**Contenders:** ring jetty adapter, http-kit

ring jetty adapter and http-kit looks equivalent and have similar api, going for http-kit because then I also use that as an http-client.

### Testing framework

**Decision:** clojure.test

**Contenders:** Clojure.test and Midje

- Clojure.test
  - Official clojure testing framework so it is here to stay
  - It is quite powerful and I already know how to work with it.

- Midje
  - Have better test reports
  - Reloads and reruns tests on files that have changes.
  - Uses unidomatic `=>` function for tests.
  - Apparently is not suited for stateful interactions which will happen because I'm dealing with database changes. See https://www.reddit.com/r/Clojure/comments/1viilt/clojure_testing_tddbdd_libraries_clojuretest_vs/ceu7rhb/ (the comment is 4yr old, take it with a pinch of salt).

- Going with clojure.test because the reasons in favor of midje don't seem to be strong enough.

### Routing Library

**Decision:** bidi

- There are lot of potential choices for a routing library, though only bidi and silk appears to work with both clojure and clojurescript, this is important because our frontend is in clojurescript. Choosing bidi over silk because it has more stars.

- Reference https://github.com/juxt/bidi#comparison-with-other-routing-libraries





---

**Misc Resources**

-   Leiningen integration with ragtime
     https://github.com/weavejester/ragtime/wiki/Leiningen-Integration
