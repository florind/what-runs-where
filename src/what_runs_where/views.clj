(ns what-runs-where.views
  (:use hiccup.core
        hiccup.page
        clj-time.local
        clj-time.format))

(defn server-casette [status]
  (let [srv-name (key status)
        srv-html-name (str "<a href=" srv-name " style='color: white;white-space:pre-wrap'>"
                             (if (> (count srv-name) 24) (str (subs srv-name 0 23) "<br/>" (subs srv-name 23)) srv-name) "</a>")
        stats (vals (val status))]
    (html
	    [:div {:class "col-lg-2 col-sm-4 col-12"}
	     [:div.well.well-small
        [:center
         (if (some #((complement nil?) (re-find #"^error" %)) stats) 
           (if (every? #((complement nil?) (re-find #"^error" %)) stats) [:span {:class "label label-danger"} srv-html-name]
             [:span {:class "label label-warning"} srv-html-name])
         [:span {:class "label label-success"} srv-html-name])]
	      [:hr.horline]
       (map 
         #(html [:small [:span3 (str (key %) ": " (val %))]] [:br]) (val status))
      ]])))

(defn env-rows [srv-list]
	(map 
    (fn [%] (html [:div.row
      (map 
        #(server-casette %) %)])) (partition-all 6 srv-list)))

(defn header []
   (html [:html 
          [:head 
           (include-css "//netdna.bootstrapcdn.com/bootstrap/3.0.0-rc1/css/bootstrap.min.css") 
           (include-css "/css/site.css")]]))  

(defn footer []
  (html [:div#footer
            [:div.container
             [:p.muted.credit (html "&copy; 2013&nbsp;&nbsp;&nbsp;" [:a {:href "/help"} "Help"])]]]
        (include-js "http://code.jquery.com/jquery-latest.js")
        (include-js "//netdna.bootstrapcdn.com/bootstrap/3.0.0-rc1/js/bootstrap.min.js")))

(defn overview-page [server-map fun]
  (let [srv-lst (flatten (map #(:servers %) (vals server-map)))]
   (html 
     (header)
	     [:body
       [:div#wrap
		     [:div.container
		      [:div.page-header [:h1 [:center "What Runs Where?"]]]
	        [:div.row 
	         [:div.row {:align "center"}
	          [:strong 
	           [:span {:class "label label-info"} (count srv-lst) " service(s)"] "  On " (clj-time.local/format-local-time (clj-time.local/local-now) :rfc822)]]]
          (pmap 
            #(html
               [:hr.horline]
               [:div.row [:h5 (key %)]]
               (env-rows (fun (val %)))) server-map)]
       [:div#push]]
	      (footer)
      ])))

(defn help-page []
  (html
    (header)
    [:body 
     [:div#wrap
	     [:div.container
	      [:div.row 
	       [:div.span12 
          [:br][:br][:strong "API (JSON):&nbsp;&nbsp;&nbsp;" [:a {:href "/api/services"} "/api/services"]]
          [:br][:br][:span "Source code:" [:a {:href "http://github.com/florind/what-runs-where"} "http://github.com/florind/what-runs-where"]]
          [:br][:br][:span "Contact:" [:a {:href "http://github.com/florind"} "http://github.com/florind"]]
          [:br][:br][:span [:a {:href "/"} "Home"]]
         ]]]
      [:div#push]]
    (footer)]))
