// sockjs-client expects Node's `global` name in some Vite browser bundles.
// Define it before the app imports SockJS so the package can initialize safely.
declare global {
  // eslint-disable-next-line no-var
  var global: typeof globalThis;
}

globalThis.global ??= globalThis;

export {};
