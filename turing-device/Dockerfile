FROM livingobjects/jre8
EXPOSE 2222

VOLUME /tmp

#时区问题
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add --no-cache tzdata \
    && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ENV JAVA_OPTS "-Xms512m -Xmx1g"
#引用dockerfile插件的参数
ARG JAR_FILE
ADD ./target/turing-device.jar /app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN bash -c 'touch /app.jar'
ENTRYPOINT java $JAVA_OPTS -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -jar /app.jar
