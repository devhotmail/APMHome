import { expect } from './testHelper'
import { JiuGongGe as JGG } from 'utils/helpers'


describe('JiuGongGe Coord Utils', () => {

  const ValueMap = {
    L: 1,
    R: 2,
    HC: 1.5,
    T: 0,
    B: 1,
    VC: 100
  }

  it('should parse input correctly, in loose mode', () => {

    expect(JGG.parse('akjs')).to.deep.equal([,,])
    expect(JGG.parse('lb')).to.deep.equal(['L', 'B'])
    expect(JGG.parse('bl')).to.deep.equal(['L', 'B'])
    expect(JGG.parse('bldfsdf')).to.deep.equal(['L', 'B'])
    expect(JGG.parse('vchc')).to.deep.equal(['HC', 'VC'])
    expect(JGG.parse('vcl')).to.deep.equal(['L', 'VC'])
    expect(JGG.parse('thc')).to.deep.equal(['HC', 'T'])
    expect(JGG.parse('qwerthqwerc')).to.deep.equal(['R', 'T'])

  })

  it('should parse input correctly, in strick mode', () => {

    expect(() => JGG.parse('akjs', { strict: true })).to.throw
    expect(JGG.parse('lb', { strict: true })).to.deep.equal(['L', 'B'])
    expect(JGG.parse('bl', { strict: true })).to.deep.equal(['L', 'B'])
    expect(() => JGG.parse('bldfsdf', { strict: true })).to.throw
    expect(JGG.parse('vchc', { strict: true })).to.deep.equal(['HC', 'VC'])
    expect(JGG.parse('vcl', { strict: true })).to.deep.equal(['L', 'VC'])
    expect(JGG.parse('thc', { strict: true })).to.deep.equal(['HC', 'T'])
    expect(() => JGG.parse('qwerthqwerc', { strict: true })).to.throw

  })

  it('should parse input correctly, with a value map supplied', () => {

    expect(JGG.parse('akjs', { valueMap: ValueMap })).to.deep.equal([,,])
    expect(JGG.parse('lb', { valueMap: ValueMap })).to.deep.equal([1, 1])
    expect(JGG.parse('bl', { valueMap: ValueMap })).to.deep.equal([1, 1])
    expect(JGG.parse('bldfsdf', { valueMap: ValueMap })).to.deep.equal([1, 1])
    expect(JGG.parse('vchc', { valueMap: ValueMap })).to.deep.equal([1.5, 100])
    expect(JGG.parse('vcl', { valueMap: ValueMap })).to.deep.equal([1, 100])
    expect(JGG.parse('thc', { valueMap: ValueMap })).to.deep.equal([1.5, 0])
    expect(JGG.parse('qwerthqwerc', { valueMap: ValueMap })).to.deep.equal([2, 0])

  })

})
