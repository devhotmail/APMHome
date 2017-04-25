/* @flow */
import React from 'react'

import DataProvider from './DataProvider'
import SizeProvider from './sizeProvider'
import Chart from './chart'

import styles from './styles.scss'

export default SizeProvider(DataProvider(Chart))
