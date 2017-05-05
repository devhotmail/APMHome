// @flow
import React from 'react'
import * as Immutable from 'immutable'

const { is } = Immutable

type O = {
  [string | number]: any
} | void

export default
class ImmutableComponent<DefaultProps: O, Props: O, State: O> extends React.Component<DefaultProps, Props, State> {
  static defaultProps: DefaultProps
  state: State
  shouldComponentUpdate(nextProps: Props, nextState: State) {
    if (nextProps === this.props && nextState === this.state) {
      return false
    }
    nextProps = nextProps || {}
    nextState = nextState || {}

    const thisProps = this.props || {}, thisState = this.state || {}
    if (Object.keys(thisProps).length !== Object.keys(nextProps).length ||
        Object.keys(thisState).length !== Object.keys(nextState).length) {
      return true;
    }

    for (const key in nextProps) {
      if (!is(thisProps[key], nextProps[key])) {
        return true;
      }
    }

    for (const key in nextState) {
      if (thisState[key] !== nextState[key] || !is(thisState[key], nextState[key])) {
        return true;
      }
    }
    return false;
  }
}
