
import i18n from 'i18next'
import XHR from 'i18next-xhr-backend'
import Cache from 'i18next-localstorage-cache'
import LanguageDetector from 'i18next-browser-languagedetector'
import conf from 'config'

let isProd = conf.env === 'prod'

i18n
  .use(XHR)
  .use(Cache)
  .use(LanguageDetector)
  .init({
    fallbackLng: 'en',
    ns: ['common'],
    defaultNS: 'common',
    debug: conf.i18n.debug,
    cache: {
      enabled: conf.i18n.cache,
    },
    backend: {
      loadPath:  isProd ? '/geapm/react/FailureAnalysis/assets/locales/{{lng}}/{{ns}}.json' : 'assets/locales/{{lng}}/{{ns}}.json'
    },
    interpolation: {
      escapeValue: false, // not needed for react
      formatSeparator: ',',
      format: function(value, format, lng) {
        if (format === 'uppercase') return value.toUpperCase()
        return value
      }
    }
  })

export default i18n