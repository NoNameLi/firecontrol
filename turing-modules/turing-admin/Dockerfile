FROM livingobjects/jre8
VOLUME /tmp

#时区问题
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add --no-cache tzdata \
    && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ADD ./target/turing-admin.jar app.jar
ADD wait-for-it.sh /wait-for-it.sh
RUN bash -c 'touch /app.jar'
RUN bash -c 'chmod 777 /wait-for-it.sh'
ENTRYPOINT ["java","-Xms128m","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-Dfile.encoding=UTF-8","-jar","/app.jar"]



