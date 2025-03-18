# Removing Module Info

While working with module-info the issue with a lot of libraries having no modularity happened, while being able to
handle some by either discarding or checking alternatives, I found myself doing a lot for no reason, enters the
following [article](https://changenode.com/fomo-java-modules/).

The lack of documentation and adoption of module-info was also an element, At first I also naively thought it would
somehow handle transitive dependencies, but it actually doesn't.

The main motivation was initially transitive dependencies issue, and while looking how some languages handle it e.g
GoLang where it chooses the lowest version, I found a bunch of pom configs wouldn't hurt.

