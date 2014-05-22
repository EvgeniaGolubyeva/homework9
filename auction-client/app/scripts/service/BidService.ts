/// <reference path="../refs.ts" />

'use strict';

module auction.service {

    import m = auction.model;

    export interface IBidService {
        placeBid: (bid: m.Bid) => void;
        watchBids: (callback: (response: any) => void) => void;
        unwatchBids: (callback: (response: any) => void) => void;
    }

    export class BidService implements IBidService {
        private webSocket: WebSocket;
        private callbacks: Array<(product: m.Product) => void> = [];

        constructor() {
            this.webSocket = new WebSocket('ws://localhost:8080/auction/server/bid');
            this.webSocket.onmessage = (event) => this.callbacks.forEach(
                (c) => c(JSON.parse(event.data)));
        }

        public placeBid(bid: m.Bid) {
            this.webSocket.send(JSON.stringify(bid));
        }

        watchBids(callback: (response: any) => void): void {
            this.callbacks.push(callback);
        }

        unwatchBids(callback: (response: any) => void): void {
            _.pull(this.callbacks, callback);
        }
    }

    angular.module('auction').service('BidService', BidService);
}