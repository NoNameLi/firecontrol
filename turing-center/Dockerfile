FROM livingobjects/jre8
VOLUME /tmp

#时区问题
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add --no-cache tzdata \
    && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ADD ./target/turing-center.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Xms64m","-Xmx256m","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
