FROM livingobjects/jre8
EXPOSE 2222

VOLUME /tmp

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# 修改容器时区为北京时间
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

ENV JAVA_OPTS "-Xms2g -Xmx2g"
#引用dockerfile插件的参数
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN bash -c 'touch /app.jar'
ENTRYPOINT java $JAVA_OPTS -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -jar /app.jar
