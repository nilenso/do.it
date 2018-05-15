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
     [[https://di.nmfay.com/postgres-vs-mysql]{.underline}](https://di.nmfay.com/postgres-vs-mysql)
     (I'm yet to read the article)

### **Migration Library:** Ragtime

-   Major contenders for the migration library were
     [[ragtime]{.underline}](https://github.com/weavejester/ragtime)
     and [[migratus]{.underline}](https://github.com/yogthos/migratus),
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
     [[https://adambard.com/blog/clojure-migration-libraries/]{.underline}](https://adambard.com/blog/clojure-migration-libraries/)

---

**Misc Resources**

-   Leiningen integration with ragtime
     https://github.com/weavejester/ragtime/wiki/Leiningen-Integration
