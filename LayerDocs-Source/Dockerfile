# Build stage via Gradle
FROM gradle:8.14.3-jdk17 AS builder

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends nodejs npm \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /app && chown -R gradle:gradle /app

USER gradle

WORKDIR /app

COPY --chown=gradle:gradle . /app

# Build the distribution zip
RUN gradle --no-daemon distZip

WORKDIR build/distributions
RUN unzip layerdocs.zip && rm layerdocs.zip

# Run stage
FROM ghcr.io/puppeteer/puppeteer:24.15.0 AS runner

ENV LD_NPM_PREFIX="/home/pptruser" \
    NODE_PATH="/home/pptruser/node_modules"

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-17-jre-headless \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

USER pptruser
WORKDIR /app
COPY --from=builder /app/build/distributions/layerdocs layerdocs
ENV PATH="/app/layerdocs/bin:${PATH}"

ENTRYPOINT ["layerdocs"]

LABEL org.opencontainers.image.vendor="LayerDocs"
LABEL org.opencontainers.image.title="LayerDocs Docker image"
LABEL org.opencontainers.image.description="Versatile Markdown-based typesetting system."
LABEL org.opencontainers.image.authors="SatyamPote"
LABEL org.opencontainers.image.url="https://layerdocs.com"
LABEL org.opencontainers.image.source="https://github.com/SatyamPote/layerdocs"
LABEL org.opencontainers.image.documentation="https://layerdocs.com/docs/"
LABEL org.opencontainers.image.licenses="GPL-3.0"
