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
 *  @version $Id: jnlp-opener.php 1056 2007-02-28 07:08:36Z vlads $
 */

    header('Content-Type: application/x-java-jnlp-file');

    $appURL = $_GET['app-url'];

    $baseURL = str_replace('/'.basename(__FILE__), '', $_SERVER['SCRIPT_NAME']);
    // This base one level UP from this script location
    $baseURL = ereg_replace('/[a-zA-Z_0-9\-]+$', '', $baseURL);

    // Can't use .. in Url, Use ~ in the beginning
    do {
        $pos = strpos($appURL, '~');
        if ($pos === 0) {
            $baseURL = ereg_replace('/[a-zA-Z_0-9\-]+$', '', $baseURL);
            $appURL = substr($appURL, 1);
        }
    } while($pos === 0);

    $jadURL = 'http://' . $_SERVER['HTTP_HOST'] . $baseURL . $appURL . '.jad';

    $patern = '<!--jadRewrite-->';

    $jnlpFileName = "../demo.jnlp";
    $fh = fopen($jnlpFileName, 'r');
    $xml = fread($fh, filesize($jnlpFileName));
    fclose($fh);
    $xml = ereg_replace($patern . '.+' . $patern, '<argument>' . $jadURL . '</argument>', $xml);
    echo($xml);
?>
