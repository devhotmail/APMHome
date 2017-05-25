import React, { PureComponent } from 'react'
import EditBlock from 'dew-editblock'

import pic from './pic.png'

import styles from './styles.scss'

const initialValue = '80'

function handleChange (value) {
  console.log(value)
}

export default class One extends PureComponent {
  render () {
    return (
      <div className={styles.wrapper} style={{color: 'red'}}>
        <div className={styles.title}>
          <div>条件1</div>
          <div>开机率 95%</div>
          <div>开机率 80%</div>
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
    )
  }
}
