## Dew React Integration

Steps for using React in Porject Dew:

### Step1: Make a copy for this folder in our React root dir `src/react`

### Step2: Modify xhtml to mount your React app

Find your target xhtml, make a copy for your target xhtml in the same folder and dont forget to backup this xhtml. Replace your new xhtml content with the code below:

```xhtml
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:jsf="http://xmlns.jcp.org/jsf"
                xmlns:h="http://java.sun.com/jsf/html"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:p="http://primefaces.org/ui"
                template="/WEB-INF/template-react.xhtml">

  <ui:composition xmlns="http://www.w3.org/1999/xhtml"
                  xmlns:jsf="http://xmlns.jcp.org/jsf"
                  xmlns:h="http://java.sun.com/jsf/html"
                  xmlns:f="http://java.sun.com/jsf/core"
                  xmlns:ui="http://java.sun.com/jsf/facelets"
                  xmlns:p="http://primefaces.org/ui"
                  template="/react/${YOUR_REACt_APP_DIR}/index.xhtml">
  </ui:composition>
</ui:composition>
```

The sample xhtml file is `/src/main/webapp/helloWorld.xhtml`

### Step3: Using `yarn install` to get your dependencies

### Step4: Scripts for better dev experience

* `npm run dev` for local dev in Node environment
* `npm run watch` for local dev in Java environment
* `npm run build` for build assets

EOF
