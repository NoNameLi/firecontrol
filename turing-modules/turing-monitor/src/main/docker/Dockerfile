FROM livingobjects/jre8
VOLUME /tmp

ADD turing-monitor.jar app.jar
ADD wait-for-it.sh /wait-for-it.sh
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add --no-cache tzdata \
    && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone && \
    sh -c 'touch /app.jar' && \
    bash -c 'chmod 777 /wait-for-it.sh'
CMD exec java  -Dfile.encoding=UTF-8  -Djava.security.egd=file:/dev/./urandom -jar /app.jar