export function formatData (data, root) {
  const result = data.map(n => {
    const {
      owner_id, owner_name,
      man_hour, repair, maintenance, meter, inspection,
      score, work_order
    } = n

    const hours = ['repair', 'maintenance', 'meter', 'inspection']
    .map(c => n[c] / root[c])

    const rate = ['score'].map(c => n[c] / root[c])

    const orders = ['work_order'].map(c => n[c] / root[c])

    return {
      id: owner_id,
      name: owner_name,
      hours,
      rate,
      orders,
      data: n
    }
  })

  return result
}
