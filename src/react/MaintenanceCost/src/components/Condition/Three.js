import React, { PureComponent } from 'react'
import EditBlock from 'dew-editblock'

import pic from './pic.png'

import styles from './styles.scss'

const initialValue = '80'

function handleChange (value) {
  console.log(value)
}

export default class Three extends PureComponent {
  render () {
    return (
      <div className={styles.three} style={{color: 'green'}}>
        <div>条件3</div>
        <div className={styles.main}>
          <div className={styles.title}>
            <div style={{color: 'yellow'}}>开机率 95%</div>
            <div style={{color: 'yellow'}}>开机率 80%</div>
          </div>
          <img className={styles.img} src={pic} />
          <div className={styles.content}>
            <span>开机率 &lt;</span>
            <EditBlock
              className={styles.block}
              onChange={handleChange}
              initialValue={initialValue}
              sign="%" />
          </div>
        </div>
        <div className={styles.main}>
          <div className={styles.title}>
            <div style={{color: 'red'}}>购买成本 10%</div>
          </div>
          <img className={styles.img} src={pic} />
          <div className={styles.content}>
            <div>
              <div className={styles.content}>
                <span>人力+备件 &gt; 购买成本</span>
                <EditBlock
                  className={styles.block}
                  onChange={handleChange}
                  initialValue={initialValue}
                  sign="%" />
              </div>
              <div className={styles.content}>偏差不超过1%</div>
            </div>
          </div>
        </div>

      </div>
    )
  }
}
