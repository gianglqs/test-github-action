/*******************************************************
Copyright (C) - DREVER International

This file is part of Malis 3 project.

Malis 3 source code can not be copied and/or distributed without the express permission of DREVER International
*******************************************************/

import { renderTestUI } from '@/utils/testing/renderTestUI'
import { AppFooter } from '@/components'

import { RenderResult, cleanup } from '@testing-library/react'

import { FOOTER as footerHeight } from '@/styles/vars/size'

/*
 * TEST CASES
 * 1. Should be rendered without crashing
 * 2. Should be rendered a text: "© Copyright -y DREVER INTERNATIONAL" and -y is current year
 * 3. The text should be centered and height footer is equal footerHeight variable
 */

describe('<AppFooter />', () => {
  const year = new Date().getFullYear()
  const footerText = `© Copyright ${year} DREVER INTERNATIONAL`

  let wrapper: RenderResult

  beforeEach(() => {
    wrapper = renderTestUI(<AppFooter />)
  })
  afterEach(cleanup)

  test('should be rendered without crashing', () => {
    expect(wrapper.container).toMatchSnapshot()
  })

  test('should be rendered a text: "© Copyright -y DREVER INTERNATIONAL" and -y is current year', () => {
    expect(wrapper.getByTestId('footer-test')).toHaveTextContent(footerText)
  })

  test('The text should be centered and height to equal FOOTER variable', () => {
    expect(wrapper.getByTestId('footer-test')).toHaveStyle({
      height: `${footerHeight}px`,
      width: '100%',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center'
    })
  })
})
