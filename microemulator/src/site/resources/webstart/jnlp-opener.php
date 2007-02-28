<?php
/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Vadym Pinchuk
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id$
 */

    header('Content-Type: application/x-java-jnlp-file');

    $jadURL = 'http://' . $_GET['app-url'] . '.jad';

    $patern = '<!--jadRewrite-->';

    $jnlpFileName = "microemu-webstart/demo.jnlp";
    $fh = fopen($jnlpFileName, 'r');
    $xml = fread($fh, filesize($jnlpFileName));
    fclose($fh);
    $xml = ereg_replace($patern . '.+' . $patern, '<argument>' . $jadURL . '</argument>', $xml);
    echo($xml);
?>
