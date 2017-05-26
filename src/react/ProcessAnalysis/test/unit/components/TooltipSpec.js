import React from 'react'
import _ from 'lodash'
import { expect } from 'chai'
import sinon from 'sinon'
import { mount, shallow, render } from 'enzyme'
import Perf from 'react-addons-perf'
import Tooltip from 'components/Tooltip'
import colors from 'colors'


describe('<Tooltip />', () => {

  const Child = () => <div className="child">child</div>

  beforeEach(() => {
    Perf.start()
  })

  afterEach(function() {
    Perf.stop()
    console.info('Perf: '.green + this.currentTest.title.blue + ': ' )
    Perf.printInclusive(Perf.getLastMeasurements())
  })

  it('performance benchmark',function(done) {
    this.timeout(20)
    let wrapper = render(<Tooltip/>)
    expect(wrapper.html()).to.not.be.null
    done()
  })

  it('should be able to render its children', function() {
    let wrapper = shallow(<Tooltip>
      <div className="child">tooltip text</div>
    </Tooltip>)
    expect(wrapper.find('.child').text()).to.equal('tooltip text')
  })

  it('should be able to render a child component passing through props', function() {
    let wrapper = shallow(<Tooltip component={Child}/>)
    expect(wrapper.find('.child').text()).to.equal('child')
  })

  it('should ignore children when `component` prop is supplied', function() {
    let wrapper = shallow(<Tooltip component={Child}><span className="should-not-be-rendered"></span></Tooltip>)
    expect(wrapper.find('.child').text()).to.equal('child')
    expect(wrapper.find('.should-not-be-rendered')).to.have.length(0)
  })

})
