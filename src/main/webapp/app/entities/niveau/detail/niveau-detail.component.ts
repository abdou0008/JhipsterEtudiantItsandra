import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INiveau } from '../niveau.model';

@Component({
  selector: 'jhi-niveau-detail',
  templateUrl: './niveau-detail.component.html',
})
export class NiveauDetailComponent implements OnInit {
  niveau: INiveau | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ niveau }) => {
      this.niveau = niveau;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
