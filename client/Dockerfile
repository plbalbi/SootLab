# Using Docker multi-stage builds. Requires docker >= 17.05
# https://docs.docker.com/develop/develop-images/multistage-build/

FROM nginx
# Clean default NGINX statics
RUN rm /usr/share/nginx/html/*
RUN rm /etc/nginx/conf.d/*
# Copy all statics, including /node_modules directory
COPY . /usr/share/nginx/html/
# Copy NGINX config file
COPY client.conf /etc/nginx/conf.d/
