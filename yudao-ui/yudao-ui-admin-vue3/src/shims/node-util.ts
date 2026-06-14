const inspectCustom = typeof Symbol === 'function' ? Symbol.for('nodejs.util.inspect.custom') : undefined

export const inspect = Object.assign((value: unknown) => String(value), {
  custom: inspectCustom
})

export default {
  inspect
}
