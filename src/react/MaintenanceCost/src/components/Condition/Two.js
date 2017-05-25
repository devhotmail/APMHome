import React, { PureComponent } from 'react'
import EditBlock from 'dew-editblock'

import pic from './pic.png'

import styles from './styles.scss'

const initialValue = '80'

function handleChange (value) {
  console.log(value)
}

export default class Two extends PureComponent {
  render () {
    return (
      <div className={styles.wrapper} style={{color: 'blue'}}>
        <div className={styles.title}>
          <div>条件2</div>
          <div>购买成本 10%</div>
        </div>
        <img className={styles.img} src={pic} />
        <div className={styles.content}>
          <span>人力+备件 &gt; 购买成本</span>
          <EditBlock
            className={styles.block}
            onChange={handleChange}
            initialValue={initialValue}
            sign="%" />
        </div>
      </div>
    )
  }
}
