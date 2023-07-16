import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NiveauDetailComponent } from './niveau-detail.component';

describe('Niveau Management Detail Component', () => {
  let comp: NiveauDetailComponent;
  let fixture: ComponentFixture<NiveauDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NiveauDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ niveau: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NiveauDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NiveauDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load niveau on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.niveau).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
