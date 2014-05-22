/// <reference path="../refs.ts" />

'use strict';

module auction.model {
    export class Bid {
        public id: number;
        public amount: number;
        public user;
        public product;
    }
}
