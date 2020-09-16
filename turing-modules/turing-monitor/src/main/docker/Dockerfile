FROM livingobjects/jre8
VOLUME /tmp

ADD turing-monitor.jar app.jar
ADD wait-for-it.sh /wait-for-it.sh
# 修改容器时区为北京时间
RUN apk --no-cache add tzdata  && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    sh -c 'touch /app.jar' && \
    bash -c 'chmod 777 /wait-for-it.sh'
CMD exec java  -Dfile.encoding=UTF-8  -Djava.security.egd=file:/dev/./urandom -jar /app.jar