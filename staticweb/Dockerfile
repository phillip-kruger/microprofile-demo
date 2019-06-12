FROM nginx:alpine

COPY default.conf /etc/nginx/conf.d/default.conf
COPY src/main/resources/META-INF/resources/index.html /usr/share/nginx/html/index.html
COPY src/main/resources/META-INF/resources/generic.html /usr/share/nginx/html/generic.html
COPY src/main/resources/META-INF/resources/elements.html /usr/share/nginx/html/elements.html
COPY src/main/resources/META-INF/resources/50x.html /usr/share/nginx/html/50x.html
COPY src/main/resources/META-INF/resources/images/ /usr/share/nginx/html/images/
COPY src/main/resources/META-INF/resources/assets/ /usr/share/nginx/html/assets/
