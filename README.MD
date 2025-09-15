# RESTEasy Website

## Getting Started

These instructions will get you a copy of the RESTEasy website up and running on your local machine for development and testing purposes.

### Installation

The site is built using [Roq](https://iamroq.com/) and [GitHub Pages](https://pages.github.com/). To build and run the site locally, all you need is a JDK and Maven:

```bash
$ mvn package quarkus:dev
```

The site will be available at [http://localhost:8080](http://localhost:8080).

## Writing blog

Add a blog entry:
- create blog entry under [content/posts](https://github.com/resteasy/resteasy.dev/tree/master/content/posts)
  and name the file with `yyyy-mm-dd-title.md'`
- add these layout and title settings before blog content in your blog entry file. Here is an [example](https://github.com/resteasy/resteasy.dev/blob/master/content/posts/2025-03-19-red-hat-ibm.md) showing what you need to add at the top of the file.

```
---
layout:    post
title:     "Blog Title"
subtitle:  ""
date:      2019-10-30 08:07:00
author:    "Java Robot"
---

Blog content line 1
Blog Content line 2
```

## Release

To announce a release, you will need to update these files:

- Create a new blog post announcing the release. The post will automatically show up in [index.md](https://github.com/resteasy/resteasy.dev/blob/master/content/index.md)
- Add the release information to [data/releases.yaml](https://github.com/resteasy/resteasy.dev/blob/main/data/releases.yaml). An entry for the release will be automatically generated in [downloads.html](https://github.com/resteasy/resteasy.dev/blob/master/content/downloads.md) and [docs.html](https://github.com/resteasy/resteasy.dev/blob/master/content/docs.html)
