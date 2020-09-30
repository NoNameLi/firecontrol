FROM livingobjects/jre8
VOLUME /tmp

# 修改容器时区为北京时间
RUN apk --no-cache add tzdata  && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

ADD turing-datacollection.jar app.jar
ADD wait-for-it.sh /wait-for-it.sh
RUN sh -c 'touch /app.jar'
RUN bash -c 'chmod 777 /wait-for-it.sh'
CMD exec java -Dfile.encoding=UTF-8 -jar /app.jar

