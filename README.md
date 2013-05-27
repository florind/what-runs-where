# What versions are these web services running?

What-runs-where is a service that aggregates and organizes the release versions of any number of web services which choose to publish it. It is useful when you have several testing&prod environments where several versions of your web services are running and you want to have a quick overview of what runs where.

## Usage

Install Lein from http://leiningen.org<br/>
Build and package the binary with:

<pre>
$ lein clean
$ lein uberjar
</pre>

##Running
WrW is itself a web service. It publishes its version aggregations as a web service presented both as a rich HTML page or a JSON API. To start the server: 

<pre>
$ java -jar what-runs-where-0.1.0-SNAPSHOT-standalone.jar
</pre>

This command starts a Jetty webserver that runs at port 8000. Use http://localhost:8000 to reach the service webpage. Use http://localhost:8000/api/services for the JSON API.

## Configuration
WrW configures itself from a properties file where services are defined. The services are grouped conveniently behind labels allowing clustering of environments e.g. staging and production. 
Note the inherent constraints of the Java Properties file.
<pre>  
Cool\ Service=
{
    :servers ["http://stackoverflow.com"], 
    :parsers [
		{
			:path "/",
			:regexp #"&lt;div id=\\\\"svnrev\\\\">\\s*(.*)\\\\s*&lt;/div&gt;", 
			:name "Stackoverflow"
		}
	]
}
</pre>
The configuration needs to be inlined in the properties file. See <https://github.com/florind/what-runs-where/test/what_runs_where/test/config-test.properties> for how inlining works with the above example. 

Cool\ Service is the group label
:servers defines a list of services for which a version can be extracted.<br/>
:parsers is a list of version parsers since a web service may publish more than its own version, possibly showing versions of submodules and/or dependent services.<br/>
:path is the path to the page whose body contains the version string<br/>
:regexp the regex to parse the said version string
:name the human readable name of the service.


# Built with

![Clojure](http://clojure.org/space/showimage/clojure-icon.gif "http://clojure.org") ![Bootstrap](http://twitter.github.com/bootstrap/assets/img/bs-docs-responsive-illustrations.png "http://twitter.github.com/bootstrap/")

## License

WrW is distributed under the terms of the MIT License.